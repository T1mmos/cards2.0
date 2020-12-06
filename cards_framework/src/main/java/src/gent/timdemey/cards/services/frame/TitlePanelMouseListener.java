package gent.timdemey.cards.services.frame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.interfaces.IFrameService;

public class TitlePanelMouseListener implements MouseListener, MouseMotionListener
{
    private int sx, sy;
    private int fx, fy;
    
    @Override
    public void mouseDragged(MouseEvent e)
    {
        IFrameService fServ = Services.get(IFrameService.class);
        
        // first ensure that the frame isn't maximized, and if a change is necessary, position the frame
        // so the mouse grabs the title bar exactly in the middle
        {
            JFrame frame = fServ.getFrame();
            int fx2 = frame.getX();
            int fy2 = frame.getY();
            fServ.unmaximize();            
            int fx3 = frame.getX();
            int fy3 = frame.getY();
            
            if (fx2 != fx3 || fy2 != fy3)
            {
                // the unmaximize had an effect, so set the frame's X coordinate so the mouse is in the center
                int fw = frame.getWidth();                
                fx = e.getX() - fw / 2;    
            }
        }
        
        int dx = e.getXOnScreen() - sx;
        int dy = e.getYOnScreen() - sy;
            
        int x = fx + dx;
        int y = fy + dy;
        
        
        fServ.setLocation(x, y);
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        if (e.getClickCount() == 2)
        {
            IFrameService fServ = Services.get(IFrameService.class);
            fServ.maximize();
        }
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        IFrameService fServ = Services.get(IFrameService.class);
        JFrame frame = fServ.getFrame();
        
        sx = e.getXOnScreen();
        sy = e.getYOnScreen();
        
        fx = frame.getX();
        fy = frame.getY();       
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        if (e.getYOnScreen() < 3)
        {
            IFrameService fServ = Services.get(IFrameService.class);
            fServ.maximize();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }
}
