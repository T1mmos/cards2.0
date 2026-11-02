package gent.timdemey.cards.services.sound;

/**
 * A fully decoded sound in the mixer's output format: interleaved 16-bit signed stereo at {@link SoundMixer#SAMPLE_RATE}.
 */
public final class PcmSample
{
	public final short[] data;
	public final int frames;

	public PcmSample(short[] data)
	{
		this.data = data;
		this.frames = data.length / SoundMixer.CHANNELS;
	}
}
