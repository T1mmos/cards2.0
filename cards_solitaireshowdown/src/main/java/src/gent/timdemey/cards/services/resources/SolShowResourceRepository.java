package gent.timdemey.cards.services.resources;

import java.net.URL;
import java.util.List;
import java.util.Map;

import gent.timdemey.cards.SolShowPlugin;
import gent.timdemey.cards.services.contract.res.ResourceType;

public class SolShowResourceRepository extends ResourceRepository
{
    @Override    
    protected void buildUrlLists (Map<ResourceType, List<URL>> typeUrls)
    {
        super.buildUrlLists(typeUrls);
        addUrl(typeUrls, ResourceType.IMAGE, SolShowPlugin.class, "/solshow/img/");
    }
}
