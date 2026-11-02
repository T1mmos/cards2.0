package gent.timdemey.cards.services.contract;

public class LayeredArea
{
    public final Coords.Absolute abscoords_src;
    public final Coords.Absolute abscoords_dst;
    public final int endLayer;
    public final boolean mirror;

    public LayeredArea(Coords.Absolute start, Coords.Absolute end, int endLayer, boolean mirror)
    {
        this.abscoords_src = start;
        this.abscoords_dst = end;
        this.endLayer = endLayer;
        this.mirror = mirror;
    }
}
