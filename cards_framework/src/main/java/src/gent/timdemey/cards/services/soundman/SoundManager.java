package gent.timdemey.cards.services.soundman;

import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.IResourceManager;
import gent.timdemey.cards.services.ISoundManager;

public class SoundManager implements ISoundManager
{
    private static int SNDTHREAD_COUNT = 0;
    private final Executor soundExecutor;

    public SoundManager()
    {
        this.soundExecutor = Executors.newCachedThreadPool(new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                Thread thr = new Thread(r, "Sound thread #" + SNDTHREAD_COUNT++);
                thr.setDaemon(true);
                return thr;
            }
        });
    }

    @Override
    public void playSound(String id)
    {
        soundExecutor.execute(() -> {
            try
            {
                InputStream is = Services.get(IResourceManager.class).getResourceAsStream("snd/" + id + ".wav");
                AudioInputStream ais = AudioSystem.getAudioInputStream(is);
                Clip test = AudioSystem.getClip();

                test.open(ais);
                test.start();

                while (!test.isRunning())
                    Thread.sleep(10);
                while (test.isRunning())
                    Thread.sleep(10);

                test.close();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        });
    }
}
