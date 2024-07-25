package gent.timdemey.cards.services.sound;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.sound.sampled.Clip;

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
    private final IResourceNameService _ResourceNameService;
    private final IResourceCacheService _ResourceCacheService;
    private final Logger _Logger;
    
    public SoundService(
        IResourceNameService resourceNameService,
        IResourceCacheService resourceCacheService,
        Logger logger)
    {
        this._ResourceNameService = resourceNameService;
        this._ResourceCacheService = resourceCacheService;
        this._Logger = logger;
    }
    
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
        String path = _ResourceNameService.getFilePath(desc);
        _ResourceCacheService.getAudio(path); // ignore the resource
    }

    @Override
    public void play(ResourceDescriptor desc)
    {
        _Logger.info("Playing %s", desc.toString());
        
        executor.submit(() -> 
        {
            try
            {
                String path = _ResourceNameService.getFilePath(desc);
                AudioResource audioRes = _ResourceCacheService.getAudio(path);
                if (audioRes == null)
                {
                    _Logger.error("Cannot play audio: no resource for descriptor " + desc);
                    return;
                }
                
                Clip clip = audioRes.raw;   
                
                clip.stop();
                clip.setFramePosition(0);
                clip.start();
            }
            catch (Exception e)
            {
                _Logger.error(e);
            }
        });
    }

}
