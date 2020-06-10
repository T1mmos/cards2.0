package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.contract.FontResource;
import gent.timdemey.cards.services.contract.ImageResource;

public interface IResourceService
{
    ImageResource getImage(String filename);
    FontResource getFont(String filename);
    
    void clear();
}
