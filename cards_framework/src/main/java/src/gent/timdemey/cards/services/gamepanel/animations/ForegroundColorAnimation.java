package gent.timdemey.cards.services.gamepanel.animations;

import java.awt.Color;

import gent.timdemey.cards.services.scaling.IScalableComponent;

public class ForegroundColorAnimation implements IAnimation 
{
    final Color color_start;
    final Color color_end;
    
    public ForegroundColorAnimation (Color start, Color end)
    {
        this.color_start = start;
        this.color_end = end;
    }

    @Override
    public void tick(double frac, IScalableComponent<?> comp)
    {
        int r = (int) ( (1.0 - frac) * color_start.getRed() + frac * color_end.getRed() );
        int g = (int) ( (1.0 - frac) * color_start.getGreen() + frac * color_end.getGreen() );
        int b = (int) ( (1.0 - frac) * color_start.getBlue() + frac * color_end.getBlue() );
        int a = (int) ( (1.0 - frac) * color_start.getAlpha() + frac * color_end.getAlpha() );
        
        Color color = new Color(r,g,b,a);
        comp.setForeground(color);
    }
}
