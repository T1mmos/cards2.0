package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.contract.GetFontResourceResponse;
import gent.timdemey.cards.services.contract.GetImageResourceResponse;

public interface IResourceService
{
    GetImageResourceResponse getImage(String filename);
    GetFontResourceResponse getFont(String filename);
    
    void clear();
}
