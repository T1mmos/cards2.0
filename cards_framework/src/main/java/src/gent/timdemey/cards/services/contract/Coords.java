package gent.timdemey.cards.services.contract;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

public class Coords
{
    private Coords()
    {
        
    }
    
    /**
     * Holds absolute coordinates.
     * 
     * @author Tim
     */
    public static class Absolute
    {
        /** Absolute x-coordinate. */
        public final int x;

        /** Absolute y-coordinate. */
        public final int y;

        /** Absolute width. */
        public final int w;

        /** Absolute height. */
        public final int h;

        /** Width of the reference rectangle. */
      //  public final int totalw;

        /** Height of the reference rectangle. */
      //  public final int totalh;

        private Absolute(int x, int y, int w, int h/*, int totalw, int totalh*/)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
           // this.totalw = totalw;
         //   this.totalh = totalh;
        }

        public Coords.Absolute translate(int dx, int dy)
        {
            int x = this.x + dx;
            int y = this.y + dy;
            
            return new Coords.Absolute(x, y, w, h /*, totalw, totalh */);
        }

        /**
         * Gets the absolute bounds.
         * 
         * @return
         */
        public Rectangle getBounds()
        {
            return new Rectangle(x, y, w, h);
        }

        /**
         * Gets the topleft point coordinates of the rectangle described by this coordinates object.
         * 
         * @return
         */
        public Point getLocation()
        {
            return new Point(x, y);
        }

        /**
         * Gets the dimension of the rectangle described by this coordinates object.
         * @return
         */
        public Dimension getSize()
        {
            return new Dimension(w, h);
        }
    }
    
    /**
     * Holds relative coordinates.
     * @author Tim
     */
    public static class Relative
    {
        /** x-coordinate relative to the width of the container. */
        public final double xrel;

        /** y-coordinate relative to the height of the container. */
        public final double yrel;

        /** Width relative to the width of the container. */
        public final double wrel;

        /** Height relative to the height of the container. */
        public final double hrel;
        
        public Relative (double xrel, double yrel, double wrel, double hrel)
        {
            this.xrel = xrel;
            this.yrel = yrel;
            this.wrel = wrel;
            this.hrel = hrel;
        }
    }

    public static Absolute getAbsolute(int x, int y, int w, int h)
    {
        return new Coords.Absolute(x, y, w, h/*, totalw, totalh*/);
    }

    public static Absolute getAbsolute(Rectangle rect)
    {
        return getAbsolute(rect.x, rect.y, rect.width, rect.height);
    }   
        
    public static Relative getRelative(double xrel, double yrel, double wrel, double hrel, int totalw, int totalh)
    {
        return new Coords.Relative(xrel, yrel, wrel, hrel);
    }
    
    public static Relative toRelative(Coords.Absolute coords, int totalw, int totalh)
    {
        double xrel = getRelative(coords.x, /*coords.*/totalw);
        double yrel = getRelative(coords.y, /*coords.*/totalh);
        double wrel = getRelative(coords.w, /*coords.*/totalw);
        double hrel = getRelative(coords.h, /*coords.*/totalh);
        return new Coords.Relative(xrel, yrel, wrel, hrel);
    }
    
    public static Relative toRelative(Coords.Absolute coords, Dimension totaldim)
    {
        return toRelative(coords, totaldim.width, totaldim.height);
    }
    
    public static Absolute toAbsolute(Coords.Relative coords, int totalw, int totalh)
    {
        int x = getAbsolute(coords.xrel, totalw);
        int y = getAbsolute(coords.yrel, totalh);
        int w = getAbsolute(coords.wrel, totalw);
        int h = getAbsolute(coords.hrel, totalh);
        return new Absolute(x, y, w, h/*, totalw, totalh*/);
    }
    
    public static Absolute toAbsolute(Coords.Relative coords, Dimension totaldim)
    {
        return toAbsolute(coords, totaldim.width, totaldim.height);
    }
    
    private static double getRelative(int nr, int totalnr)
    {
        return (1.0) * nr / totalnr;
    }

    private static int getAbsolute(double frac, int totalnr)
    {
        return (int) (frac * totalnr);
    }

    public static Coords.Absolute interpolate(double frac, Absolute coords_src, Absolute coords_dst)
    {
        /*if (coords_src.totalw != coords_dst.totalw || coords_src.totalh != coords_src.totalw)
        {
            throw new IllegalArgumentException("To interpolate absolute coordinates, the reference frames need to have equal dimensions");
        }*/
        
        int x = (int) ( (1.0 - frac) * coords_src.x + frac * coords_dst.x );
        int y = (int) ( (1.0 - frac) * coords_src.y + frac * coords_dst.y );
        int w = (int) ( (1.0 - frac) * coords_src.w + frac * coords_dst.w );
        int h = (int) ( (1.0 - frac) * coords_src.h + frac * coords_dst.h );
        
        return new Coords.Absolute(x, y, w, h/*, coords_src.totalw, coords_src.totalh*/);
    }   
}
