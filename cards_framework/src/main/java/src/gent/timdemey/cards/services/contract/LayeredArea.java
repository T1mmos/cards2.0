package gent.timdemey.cards.services.contract;

import java.awt.Point;
import java.awt.Rectangle;

public class LayeredArea
{
    public final int x;
    public final int y;    
    public final int w;
    public final int h;
    

    private final int z_norm;
    private final int z_anim;
    
    public LayeredArea(int x, int y, int w, int h, int z_norm, int z_anim)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.z_norm = z_norm;
        this.z_anim = z_anim;
    }
    
    public LayeredArea(Rectangle bounds, int z_norm, int z_anim)
    {
        this(bounds.x, bounds.y, bounds.width, bounds.height, z_norm, z_anim);
    }
    
    public Rectangle getBounds2D ()
    {
        return new Rectangle(x, y, w, h);
    }

    public Point getLocation2D()
    {
        return new Point(x, y);
    }

    public int getLayer(boolean animating)
    {
        if (animating)
        {
            return z_anim;
        }
        else
        {
            return z_norm;
        }
    }
}
