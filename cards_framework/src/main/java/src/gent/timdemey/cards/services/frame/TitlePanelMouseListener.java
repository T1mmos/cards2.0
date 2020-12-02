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
        int dx = e.getXOnScreen() - sx;
        int dy = e.getYOnScreen() - sy;
        
        int x = fx + dx;
        int y = fy + dy;

        IFrameService fServ = Services.get(IFrameService.class);
        fServ.setLocation(x, y);
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
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
