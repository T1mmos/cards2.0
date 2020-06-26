package gent.timdemey.cards.services.contract;

import java.awt.Point;
import java.awt.Rectangle;

public class LayeredArea
{
    public final int x;
    public final int y;
    public final int width;
    public final int height; 
    public final int layer;
    public final boolean mirror;

    public LayeredArea(int x, int y, int w, int h, int layer, boolean mirror)
    {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.layer = layer;
        this.mirror = mirror;
    }

    public LayeredArea(Rectangle bounds, int layer, boolean mirror)
    {
        this(bounds.x, bounds.y, bounds.width, bounds.height, layer, mirror);
    }

    public Rectangle getBounds2D()
    {
        return new Rectangle(x, y, width, height);
    }

    public Point getLocation2D()
    {
        return new Point(x, y);
    }
}
