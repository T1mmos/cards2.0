package gent.timdemey.cards.services.scaleman.text;

import java.util.UUID;

import gent.timdemey.cards.services.contract.FontResource;
import gent.timdemey.cards.services.scaleman.ScalableResource;

public class ScalableFontResource extends ScalableResource<FontResource>
{

    public ScalableFontResource(UUID id, FontResource resource)
    {
        super(id, resource);
    }

}
