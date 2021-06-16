package gent.timdemey.cards.ui.panels.menu;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.Timer;

public class MenuButtonMouseListener implements MouseListener
{
    private Map<JButton, MenuButtonAnimation> animations = new HashMap<>();
   
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        finishAnimation(e);
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        JButton button = (JButton) e.getSource();
        MenuButtonAnimation animation = animations.get(button);
        
        if (animation == null)
        {
            animation = new MenuButtonAnimation(button);
            animations.put(button, animation);            

            Timer timer = new Timer(15, animation);
            timer.start();
        }
        
        animation.setForward(true);
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        finishAnimation(e);
    }

    private void finishAnimation(MouseEvent e)
    {
        JButton button = (JButton) e.getSource();
        MenuButtonAnimation animation = animations.get(button);
        if (animation == null)
        {
            return;
        }
        
        animation.setForward(false);
    }
}
