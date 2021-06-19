package gent.timdemey.cards.services.animation;

import java.awt.Color;

import javax.swing.JComponent;

import gent.timdemey.cards.utils.ColorUtils;

public class ForegroundColorAnimation implements IAnimation
{
    final Color color_start;
    final Color color_end;

    public ForegroundColorAnimation(Color start, Color end)
    {
        this.color_start = start;
        this.color_end = end;
    }

    @Override
    public void tick(JComponent jcomp, double frac, AnimationStart animStart)
    {        
        Color color = ColorUtils.interpolate(color_start, color_end, frac);
        jcomp.setForeground(color);
        jcomp.repaint();
    }
}