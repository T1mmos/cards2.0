package gent.timdemey.cards.services.sound;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import gent.timdemey.cards.logging.Logger;

/**
 * Software mixer on top of a single, always open {@link SourceDataLine} with a small buffer.
 * <p>
 * Every {@link #play(PcmSample)} adds a new voice; the mixer thread sums all active voices per block and
 * writes the block to the line. Sounds can overlap, are never cut off, and always start at the next block,
 * so the latency is low and the same for every play.
 */
public final class SoundMixer
{
	public static final float SAMPLE_RATE = 44100f;
	public static final int CHANNELS = 2;

	/** Frames mixed and written per iteration (~5.8 ms). */
	private static final int BLOCK_FRAMES = 256;
	/** Line buffer in frames (~23 ms): the upper bound of the output latency added by Java Sound. */
	private static final int LINE_BUFFER_FRAMES = BLOCK_FRAMES * 4;
	/** Amplitude below which leading samples are considered silence and trimmed at load time. */
	private static final int SILENCE_THRESHOLD = 328; // ~1% of full scale
	/** Frames kept before the first audible sample so the attack is not clipped. */
	private static final int PRE_ROLL_FRAMES = 44; // ~1 ms

	private static final AudioFormat FORMAT = new AudioFormat(SAMPLE_RATE, 16, CHANNELS, true, false);

	private final ConcurrentLinkedQueue<PcmSample> incoming = new ConcurrentLinkedQueue<>();
	private volatile boolean running = false;
	private Thread thread;

	private static final class Voice
	{
		final PcmSample sample;
		int pos; // in samples (not frames)

		Voice(PcmSample sample)
		{
			this.sample = sample;
		}
	}

	public synchronized void start()
	{
		if (running)
		{
			return;
		}
		running = true;
		thread = new Thread(this::run, "Audio");
		thread.setDaemon(true);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}

	public synchronized void stop()
	{
		running = false;
		if (thread != null)
		{
			thread.interrupt();
			thread = null;
		}
	}

	/** Thread safe and non-blocking: the sound starts with the next mixed block. */
	public void play(PcmSample sample)
	{
		if (sample != null && sample.frames > 0)
		{
			incoming.add(sample);
		}
	}

	private void run()
	{
		SourceDataLine line;
		try
		{
			line = AudioSystem.getSourceDataLine(FORMAT);
			line.open(FORMAT, LINE_BUFFER_FRAMES * FORMAT.getFrameSize());
			line.start();
			Logger.info("Audio mixer started, line buffer %s frames (%.1f ms)", line.getBufferSize() / FORMAT.getFrameSize(),
					1000.0 * line.getBufferSize() / FORMAT.getFrameSize() / SAMPLE_RATE);
		}
		catch (Exception e)
		{
			Logger.error("Cannot open the audio output line, sound is disabled", e);
			running = false;
			return;
		}

		List<Voice> voices = new ArrayList<>();
		int[] mix = new int[BLOCK_FRAMES * CHANNELS];
		byte[] out = new byte[BLOCK_FRAMES * FORMAT.getFrameSize()];

		try
		{
			while (running)
			{
				PcmSample s;
				while ((s = incoming.poll()) != null)
				{
					voices.add(new Voice(s));
				}

				java.util.Arrays.fill(mix, 0);
				for (int v = voices.size() - 1; v >= 0; v--)
				{
					Voice voice = voices.get(v);
					short[] data = voice.sample.data;
					int n = Math.min(mix.length, data.length - voice.pos);
					for (int i = 0; i < n; i++)
					{
						mix[i] += data[voice.pos + i];
					}
					voice.pos += n;
					if (voice.pos >= data.length)
					{
						voices.remove(v);
					}
				}

				for (int i = 0, b = 0; i < mix.length; i++, b += 2)
				{
					int val = Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, mix[i]));
					out[b] = (byte) val;
					out[b + 1] = (byte) (val >> 8);
				}

				// blocks while the line buffer is full; writing silence when idle keeps the line running
				// so a new sound never has to wait for the line to (re)start
				line.write(out, 0, out.length);
			}
		}
		finally
		{
			line.stop();
			line.flush();
			line.close();
		}
	}

	/**
	 * Decodes any format supported by Java Sound to the mixer format and trims leading silence.
	 */
	public static PcmSample decode(InputStream is) throws Exception
	{
		try (AudioInputStream src = AudioSystem.getAudioInputStream(is))
		{
			AudioFormat srcFormat = src.getFormat();
			AudioFormat pcm = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, srcFormat.getSampleRate(), 16,
					srcFormat.getChannels(), srcFormat.getChannels() * 2, srcFormat.getSampleRate(), false);
			try (AudioInputStream pcmStream = AudioSystem.getAudioInputStream(pcm, src))
			{
				byte[] bytes = readAll(pcmStream);
				int srcChannels = pcm.getChannels();
				int srcFrames = bytes.length / (2 * srcChannels);
				double ratio = srcFormat.getSampleRate() / SAMPLE_RATE;
				int dstFrames = (int) Math.floor(srcFrames / ratio);

				short[] data = new short[dstFrames * CHANNELS];
				for (int f = 0; f < dstFrames; f++)
				{
					int sf = Math.min(srcFrames - 1, (int) (f * ratio));
					for (int c = 0; c < CHANNELS; c++)
					{
						int sc = Math.min(c, srcChannels - 1);
						int idx = (sf * srcChannels + sc) * 2;
						data[f * CHANNELS + c] = (short) ((bytes[idx] & 0xFF) | (bytes[idx + 1] << 8));
					}
				}

				return new PcmSample(trimLeadingSilence(data));
			}
		}
	}

	private static short[] trimLeadingSilence(short[] data)
	{
		int first = 0;
		while (first < data.length && Math.abs(data[first]) < SILENCE_THRESHOLD)
		{
			first++;
		}
		int startFrame = Math.max(0, first / CHANNELS - PRE_ROLL_FRAMES);
		int start = startFrame * CHANNELS;
		if (start == 0 || first >= data.length)
		{
			return data;
		}
		short[] trimmed = new short[data.length - start];
		System.arraycopy(data, start, trimmed, 0, trimmed.length);
		return trimmed;
	}

	private static byte[] readAll(InputStream is) throws Exception
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int n;
		while ((n = is.read(buf)) > 0)
		{
			bos.write(buf, 0, n);
		}
		return bos.toByteArray();
	}
}
