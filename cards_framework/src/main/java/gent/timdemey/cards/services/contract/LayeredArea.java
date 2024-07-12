package gent.timdemey.cards.services.contract;

import gent.timdemey.cards.services.animation.LayerRange;

public class LayeredArea
{
    public final Coords.Absolute abscoords_src;
    public final Coords.Absolute abscoords_dst;
    public final int endLayer;
    public final LayerRange startLayerRange;
    public final boolean mirror;

    public LayeredArea(Coords.Absolute start, Coords.Absolute end, LayerRange startLayerRange, int endLayer, boolean mirror)
    {
        this.abscoords_src = start;
        this.abscoords_dst = end;
        this.startLayerRange = startLayerRange;
        this.endLayer = endLayer;
        this.mirror = mirror;
    }
}
