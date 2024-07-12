package gent.timdemey.cards.utils;

import java.awt.Dimension;

public class DimensionUtils
{
    private DimensionUtils()
    {
    }
    
    public static Dimension getMinimum (Dimension minimum1, Dimension minimum2)
    {
        int w = Math.max(minimum1.width, minimum2.width);
        int h = Math.max(minimum1.height, minimum2.height);
        return new Dimension(w, h);
    }
}
