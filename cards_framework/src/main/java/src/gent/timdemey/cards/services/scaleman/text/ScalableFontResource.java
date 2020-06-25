package gent.timdemey.cards.services.scaleman.text;

import java.awt.Font;
import java.util.UUID;

import gent.timdemey.cards.services.contract.Resource;
import gent.timdemey.cards.services.scaleman.ScalableResource;

public class ScalableFontResource extends ScalableResource<Font, Resource<Font>>
{

    public ScalableFontResource(UUID id, Resource<Font> resource)
    {
        super(id, resource);
    }

    @Override
    public void rescale(int width, int height)
    {
        
        // TODO Auto-generated method stub
        
    }
}
