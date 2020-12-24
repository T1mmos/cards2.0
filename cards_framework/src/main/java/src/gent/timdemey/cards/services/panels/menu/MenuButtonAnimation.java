package gent.timdemey.cards.services.panels.menu;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import gent.timdemey.cards.utils.ColorUtils;

public class MenuButtonAnimation implements ActionListener
{
    private static final int DURATION = 250;
    private final JButton button;
    
    private Color color_start;
    private Color color_end = Color.WHITE;
    
    private long currtime = 0;
    private boolean forward = true;
    private double frac = 0;
    
    public MenuButtonAnimation(JButton button)
    {
        this.button = button;
        this.color_start = button.getForeground();
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (currtime == 0)
        {
            currtime = System.currentTimeMillis();
            return;
        }
        
        long nowtime = System.currentTimeMillis();
        long dt = nowtime - currtime;
        
        frac += (1.0 * dt / DURATION) * (forward ? 1 : - 1);        
        if (frac < 0)
        {
            frac = 0;
        }
        else if (frac > 1)
        {
            frac = 1;
        }
        
        Color color = ColorUtils.interpolate(color_start, color_end, frac);
        button.setForeground(color);
        
        currtime = nowtime;
    }
    
    public void setForward(boolean forward) 
    {
        if (this.forward == forward)
        {
            return;
        }
        
        this.forward = forward;
    }
    
    public boolean isDone()
    {
        return (!forward && frac <= 0) ||
               (forward && frac >= 1);
    }
}
