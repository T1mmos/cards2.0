package gent.timdemey.cards.services.gamepanel;

import java.awt.Color;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.Timer;

class Animator
{
    private static final class AnimInfo
    {
        private long tickStart;
        private Point startPos;
        private Point endPos;
        private int animationTime;
        private boolean dissolve;
    }

    private final Map<JComponent, AnimInfo> animations;
    private final Timer timer;

    Animator()
    {
        animations = new HashMap<>();     
        timer = new Timer(15, e -> tick());
    }

    void animate(JComponent comp, Point dst, int animationTime, boolean dissolve)
    {
        AnimInfo animInfo = new AnimInfo();
        animInfo.startPos = new Point(comp.getX(), comp.getY());
        animInfo.endPos = dst;
        animInfo.tickStart = System.currentTimeMillis();
        animInfo.animationTime = animationTime;
        animInfo.dissolve = dissolve;
        animations.put(comp, animInfo);
        
        if (!timer.isRunning())
        {
            timer.start();
        }
    }

    private void tick()
    {
        long currTickTime = System.currentTimeMillis();
        for (JComponent comp : new HashSet<>(animations.keySet()))
        {
            AnimInfo animInfo = animations.get(comp);
            long dt = currTickTime - animInfo.tickStart;
            double frac = Math.min(1.0, 1.0 * dt / animInfo.animationTime);
            
            int x = animInfo.startPos.x + (int) (frac * (animInfo.endPos.x - animInfo.startPos.x));
            int y = animInfo.startPos.y + (int) (frac * (animInfo.endPos.y - animInfo.startPos.y));
            
            
            comp.setBounds(x, y, comp.getWidth(), comp.getHeight());
            
            if (animInfo.dissolve)
            {
                Color fg = comp.getForeground();
                Color fg_new = new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), (int) ((1 - frac) * 255));
                comp.setForeground(fg_new);
            }
            
            if (frac == 1.0)
            {
                animations.remove(comp);
                if (animInfo.dissolve)
                {
                    comp.getParent().remove(comp);
                }
            }
        }

        if (animations.isEmpty())
        {
            timer.stop();
        }
    }

}
