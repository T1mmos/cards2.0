package gent.timdemey.cards.ui.components;

import java.awt.Dimension;
import java.awt.Font;
import java.util.UUID;

import gent.timdemey.cards.services.contract.res.Resource;

public final class SFontResource extends SResource<Font>
{
    public SFontResource(UUID id, Resource<Font> resource)
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
