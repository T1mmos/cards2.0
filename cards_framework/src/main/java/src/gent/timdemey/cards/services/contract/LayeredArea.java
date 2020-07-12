package gent.timdemey.cards.services.contract;

public class LayeredArea
{
    public final Coords coords;    
    public final int layer;
    public final boolean mirror;

    public LayeredArea(Coords coords, int layer, boolean mirror)
    {
        this.coords = coords;
        this.layer = layer;
        this.mirror = mirror;
    }
}
