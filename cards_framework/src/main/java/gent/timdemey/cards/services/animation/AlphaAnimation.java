package gent.timdemey.cards.services.animation;

import javax.swing.JComponent;

import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.utils.ColorUtils;

public class AlphaAnimation implements IAnimation
{
    final float alpha_start;
    final float alpha_end;

    public AlphaAnimation(float start, float end)
    {
        this.alpha_start = start;
        this.alpha_end = end;
    }

    @Override
    public void tick(JComponent jcomp, double frac, AnimationStart animStart)
    {        
        if (jcomp instanceof IHasDrawer)
        {
            IHasDrawer hasDraw = (IHasDrawer) jcomp;
            float alpha = ColorUtils.interpolate(alpha_start, alpha_end, frac);
            hasDraw.getDrawer().setForegroundAlpha(alpha);
        }
    }
}
