package gent.timdemey.cards.services.scaleman.text;

import java.awt.Font;
import java.util.UUID;

import gent.timdemey.cards.services.contract.Resource;
import gent.timdemey.cards.services.scaleman.ScalableResource;

public class ScalableFontResource extends ScalableResource<Font>
{
    public ScalableFontResource(UUID id, Resource<Font> resource)
    {
        super(id, resource);
    }

    @Override
    protected boolean canRescale()
    {
        // we always rescale fonts
        return true;
    }

    @Override
    protected Font rescaleImpl(int width, int height)
    {
        float size = (float) height;
        Font rescaled = getResource().raw.deriveFont(size);
        return rescaled;
    }
}
