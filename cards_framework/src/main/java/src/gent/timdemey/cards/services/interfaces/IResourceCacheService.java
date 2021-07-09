package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.contract.res.AudioResource;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.contract.res.ImageResource;

public interface IResourceCacheService
{
    ImageResource getImage(String filename);
    FontResource getFont(String filename);
    AudioResource getAudio(String filename);
    
    void clear();
}
