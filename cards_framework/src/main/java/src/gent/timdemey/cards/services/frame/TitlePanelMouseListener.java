package gent.timdemey.cards.services.frame;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EnumSet;

import javax.swing.JFrame;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.SnapSide;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.utils.ArrayUtils;

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
        
        if (fServ.isSnapped())
        {
            fServ.unsnap();
            
            JFrame frame = fServ.getFrame();
            int fw = frame.getWidth();                
            fx = e.getXOnScreen() - fw / 2;   
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
        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        boolean snapTop = e.getYOnScreen() < 3;
        boolean snapBottom = maxBounds.height - 3 <= e.getYOnScreen();
        boolean snapLeft = e.getXOnScreen() < 3;
        boolean snapRight = maxBounds.width - 3 <= e.getXOnScreen();  
      
        EnumSet<SnapSide> snapsides = EnumSet.noneOf(SnapSide.class);
        if (snapTop)
        {
            snapsides.add(SnapSide.TOP);
        }
        if (snapBottom)
        {
            snapsides.add(SnapSide.BOTTOM);
        }
        if (snapLeft)
        {
            snapsides.add(SnapSide.LEFT);
        }
        if (snapRight)
        {
            snapsides.add(SnapSide.RIGHT);
        }
        
        if (snapsides.size() > 0)
        {
            SnapSide[] arr =  ArrayUtils.from(snapsides, () -> new SnapSide[0]);
            IFrameService fServ = Services.get(IFrameService.class);
            fServ.snap(arr);    
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
