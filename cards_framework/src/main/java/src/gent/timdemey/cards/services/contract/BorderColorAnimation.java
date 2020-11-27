package gent.timdemey.cards.services.contract;

import java.awt.Color;

import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.text.ScalableTextComponent;
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
    public void tick(IScalableComponent comp, double frac, AnimationStart animStart)
    {
        Color color = ColorUtils.interpolate(color_start, color_end, frac);
                
        if (comp instanceof ScalableTextComponent)
        {
            ScalableTextComponent textComp = (ScalableTextComponent) comp;
            textComp.setOuterColor(color);
        }
    }
}