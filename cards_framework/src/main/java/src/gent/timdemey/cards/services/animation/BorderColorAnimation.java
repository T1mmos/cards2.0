package gent.timdemey.cards.services.animation;

import java.awt.Color;

import gent.timdemey.cards.ui.components.drawers.ScaledTextDrawer;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.utils.ColorUtils;

public class BorderColorAnimation implements IAnimation
{
    final Color color_start;
    final Color color_end;

    public BorderColorAnimation(Color start, Color end)
    {
        this.color_start = start;
        this.color_end = end;
    }

    @Override
    public void tick(IComponent comp, double frac, AnimationStart animStart)
    {
        Color color = ColorUtils.interpolate(color_start, color_end, frac);
                
        if (comp instanceof ScaledTextDrawer)
        {
            ScaledTextDrawer textComp = (ScaledTextDrawer) comp;
            textComp.setOuterColor(color);
        }
    }
}