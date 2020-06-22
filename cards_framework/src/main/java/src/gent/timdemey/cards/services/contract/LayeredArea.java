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
    public final int zorder;

    public LayeredArea(int x, int y, int w, int h, int layer, int zorder)
    {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.layer = layer;
        this.zorder = zorder;
    }

    public LayeredArea(Rectangle bounds, int layer_norm, int zorder)
    {
        this(bounds.x, bounds.y, bounds.width, bounds.height, layer_norm, zorder);
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
