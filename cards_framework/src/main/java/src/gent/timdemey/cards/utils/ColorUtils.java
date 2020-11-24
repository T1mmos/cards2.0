package gent.timdemey.cards.utils;

import java.awt.Color;

public class ColorUtils
{    
    public static Color rgb (String hex)
    {
        return Color.decode(hex);
    }
    
    public static Color rgba (String hex)
    {
        String a = hex.substring(7);
        int alpha = Integer.parseInt(a, 16);
        String rgb = "#" + hex.substring(1, 7);
        return transparent(rgb, alpha);
    }
    
    public static Color transparent()
    {
        return transparent("#000000");
    }
    
    public static Color transparent(String hex)
    {
        return transparent(hex, 0);
    }
    
    public static Color transparent(String hex, int alpha)
    {
        Color color = Color.decode(hex);
        Color tcolor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        return tcolor;
    }
    
    private ColorUtils()
    {
    }
}
