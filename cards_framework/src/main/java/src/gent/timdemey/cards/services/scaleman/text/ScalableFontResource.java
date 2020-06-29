package gent.timdemey.cards.services.scaleman.text;

import java.awt.Dimension;
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
    protected Dimension canonical(Dimension dim)
    {
        // for a font, only the height matters
        return new Dimension(0, dim.height);
    }

    @Override
    protected Font rescaleImpl(Dimension target)
    {
        float size = (float) target.height;
        Font rescaled = getResource().raw.deriveFont(size);
        return rescaled;
    }
}
