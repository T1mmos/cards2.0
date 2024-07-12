package gent.timdemey.cards.services.sound;

import java.io.ByteArrayInputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.contract.preload.IPreload;
import gent.timdemey.cards.services.contract.res.AudioResource;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IResourceNameService;
import gent.timdemey.cards.services.interfaces.ISoundService;

public class SoundService implements ISoundService, IPreload
{
    private ExecutorService executor = null;
    
    @Override
    public void preload()
    {
        executor = Executors.newSingleThreadExecutor(new ThreadFactory()
        {
            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "Audio");
            }
        });
      
        // load necessary files
        preload(ResourceDescriptors.SoundTest);
    }
    
    private void preload(ResourceDescriptor desc)
    {
        IResourceCacheService resCacheServ = Services.get(IResourceCacheService.class);
        IResourceNameService resNameServ = Services.get(IResourceNameService.class);

        String path = resNameServ.getFilePath(desc);
        resCacheServ.getAudio(path); // ignore the resource
    }

    @Override
    public void play(ResourceDescriptor desc)
    {
        executor.submit(() -> 
        {
            try
            {
                IResourceCacheService resCacheServ = Services.get(IResourceCacheService.class);
                IResourceNameService resNameServ = Services.get(IResourceNameService.class);
                String path = resNameServ.getFilePath(desc);
                AudioResource audioRes = resCacheServ.getAudio(path);
                if (audioRes == null)
                {
                    Logger.error("Cannot play audio: no resource for descriptor " + desc);
                    return;
                }
                
                // the byte array is read once from disk due to the cache service. 
                // however to support multiplay we need a new InputStream for each request to play the 
                // same resource, so we'll use a new ByteArrayInputStream on top of the byte array.
                byte[] arr = audioRes.raw;                
                @SuppressWarnings("resource")
                Clip clip = AudioSystem.getClip();
                clip.addLineListener(new LineListener() 
                {
                    public void update(LineEvent myLineEvent) {
                        if (myLineEvent.getType() == LineEvent.Type.STOP)
                        {
                            clip.close();  
                        }
                    }
                });
                ByteArrayInputStream bis = new ByteArrayInputStream(arr);
                @SuppressWarnings("resource")
                AudioInputStream ais = AudioSystem.getAudioInputStream(bis);
                clip.open(ais);
                clip.start();
            }
            catch (Exception e)
            {
                Logger.error(e);
            }
        });
    }

}
