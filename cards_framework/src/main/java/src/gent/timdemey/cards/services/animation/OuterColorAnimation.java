package gent.timdemey.cards.services.animation;

import java.awt.Color;

import javax.swing.JComponent;

import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.drawers.ScaledTextDrawer;
import gent.timdemey.cards.utils.ColorUtils;

public class OuterColorAnimation implements IAnimation
{
    final Color color_start;
    final Color color_end;

    public OuterColorAnimation(Color start, Color end)
    {
        this.color_start = start;
        this.color_end = end;
    }

    @Override
    public void tick(JComponent comp, double frac, AnimationStart animStart)
    {
        Color color = ColorUtils.interpolate(color_start, color_end, frac);
        IDrawer drawer = ((IHasDrawer) comp).getDrawer();        
        
        if (drawer instanceof ScaledTextDrawer)
        {
            ScaledTextDrawer textComp = (ScaledTextDrawer) drawer;
            textComp.setOuterColor(color);
        }
    }
}