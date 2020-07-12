package gent.timdemey.cards.services.contract;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Holds both absolute and relative coordinates and dimensions of a component in the gamepanel.
 * @author Tim
 */
public class Coords
{
    /** Absolute x-coordinate for the current size of the container. */
    public final int x;
    
    /** x-coordinate relative to the width of the container. */
    public final double xrel;
    
    /** Absolute y-coordinate for the current size of the container. */
    public final int y;
    
    /** y-coordinate relative to the height of the container. */
    public final double yrel;
    
    /** Absolute width for the current size of the container. */
    public final int width;
    
    /** Width relative to the width of the container. */
    public final double widthrel;
    
    /** Absolute height for the current size of the container. */
    public final int height; 
    
    /** Height relative to the height of the container. */
    public final double heightrel;
    
    public Coords(int x, int y, int w, int h, int totalw, int totalh)
    {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.xrel = (1.0 * x) / totalw;
        this.yrel = (1.0 * y) / totalh;
        this.widthrel = (1.0 * width) / totalw;
        this.heightrel = (1.0 * height) / totalh;
    }
    
    /**
     * Gets the absolute bounds.
     * @return
     */
    public Rectangle getBounds()
    {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Gets the absolute topleft point coordinates.
     * @return
     */
    public Point getLocation()
    {
        return new Point(x, y);
    }
}
