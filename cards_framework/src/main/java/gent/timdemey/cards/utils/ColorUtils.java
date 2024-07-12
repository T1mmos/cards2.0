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
        return transparent(color, alpha);
    }
    
    public static Color transparent(Color color, int alpha)
    {
        Color tcolor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        return tcolor;
    }
    
    private ColorUtils()
    {
    }

    public static Color interpolate(Color color_start, Color color_end, double frac)
    {
        int r = (int) interpolate(color_start.getRed(), color_end.getRed(), frac);
        int g = (int) interpolate(color_start.getGreen(), color_end.getGreen(), frac );
        int b = (int) interpolate(color_start.getBlue(), color_end.getBlue(), frac );
        int a = (int) interpolate(color_start.getAlpha(), color_end.getAlpha(), frac );
        
        Color color = new Color(r,g,b,a);
        return color;
    }
    
    public static double interpolate(int start, int end, double frac)
    {
        double result = ( (1.0 - frac) * start + frac * end );
        return result;
    }

    public static float interpolate(float start, float end, double frac)
    {
        float result = (float) ( (1.0 - frac) * start + frac * end );
        return result;
    }
    
    public static Color transparent(Color color, float alpha)
    {
        int alphaInt = (int) (255 * alpha);
        Color tcolor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alphaInt);
        return tcolor;
    }
}
