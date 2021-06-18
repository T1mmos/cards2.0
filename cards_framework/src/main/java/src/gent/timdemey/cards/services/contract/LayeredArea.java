package gent.timdemey.cards.services.contract;

public class LayeredArea
{
    public final Coords.Absolute abscoords;    
    public final int layer;
    public final boolean mirror;

    public LayeredArea(Coords.Absolute coords, int layer, boolean mirror)
    {
        this.abscoords = coords;
        this.layer = layer;
        this.mirror = mirror;
    }
}
