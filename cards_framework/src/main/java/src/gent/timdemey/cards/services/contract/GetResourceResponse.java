package gent.timdemey.cards.services.contract;

import java.awt.Dimension;

public class GetResourceResponse<R>
{
    public final R resource;
    public final Dimension canonical;
    public final boolean found;
    
    public GetResourceResponse (R resource, Dimension canonical, boolean found)
    {
        this.resource = resource;
        this.canonical = canonical;
        this.found = found;
    }
}
