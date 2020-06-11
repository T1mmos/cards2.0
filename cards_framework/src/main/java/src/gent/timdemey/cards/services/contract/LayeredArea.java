package gent.timdemey.cards.services.contract;

import java.awt.Point;
import java.awt.Rectangle;

public class LayeredArea
{
    public final int x;
    public final int y;    
    public final int width;
    public final int height;
    

    private final int layer_norm;
    private final int layer_anim;
    private final int zorder;
    
    public LayeredArea(int x, int y, int w, int h, int layer_norm, int layer_anim, int zorder)
    {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.layer_norm = layer_norm;
        this.layer_anim = layer_anim;
        this.zorder = zorder;
    }
    
    public LayeredArea(Rectangle bounds, int layer_norm, int layer_anim, int zorder)
    {
        this(bounds.x, bounds.y, bounds.width, bounds.height, layer_norm, layer_anim, zorder);
    }
    
    public Rectangle getBounds2D ()
    {
        return new Rectangle(x, y, width, height);
    }

    public Point getLocation2D()
    {
        return new Point(x, y);
    }

    public int getLayer(boolean animating)
    {
        if (animating)
        {
            return layer_anim;
        }
        else
        {
            return layer_norm;
        }
    }
    
    public int getZOrder()
    {
        return zorder;
    }
}
