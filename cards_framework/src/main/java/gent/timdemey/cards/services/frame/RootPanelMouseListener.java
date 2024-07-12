package gent.timdemey.cards.services.frame;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.interfaces.IFrameService;

public class RootPanelMouseListener implements MouseListener, MouseMotionListener
{
    private static final int BORDER = 5;
    private int sx, sy;
    private int fx, fy, fw, fh;
    // private int 
    
    private boolean isResizeN, isResizeS, isResizeE, isResizeW;
  //  private boolean isXresize, isYresize;
    private boolean resizing;
    
    private static final int[][] CURSORS = new int [][]
        {
                new int[] { Cursor.NW_RESIZE_CURSOR, Cursor.N_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR },
                new int[] { Cursor.W_RESIZE_CURSOR,  Cursor.DEFAULT_CURSOR,  Cursor.E_RESIZE_CURSOR  },
                new int[] { Cursor.SW_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR }
        };
           
    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (resizing)
        {            
            int dx = e.getXOnScreen() - sx;
            int dy = e.getYOnScreen() - sy;
            
            int x, y, h, w;
            if (isResizeE)
            {
                x = fx;
                w = fw + dx;
            }
            else if (isResizeW)
            {
                x = fx + dx;
                w = fw - dx;
            }
            else
            {
                x = fx;
                w = fw;
            }
            
            if (isResizeN)
            {
                y = fy + dy;
                h = fh - dy;
            }
            else if (isResizeS)
            {
                y = fy;
                h = fh + dy;
            }
            else
            {
                y = fy;
                h = fh;
            }            

            IFrameService fServ = Services.get(IFrameService.class);
            fServ.setBounds(x, y, w, h);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        if (!resizing)
        {
            JComponent rootPanel = (JComponent) e.getSource();
            int w = rootPanel.getWidth();
            int h = rootPanel.getHeight();
            int x = e.getX();
            int y = e.getY();
            
            isResizeN =  0 <= y && y < BORDER;
            isResizeS = h - BORDER <= y && y < h;
            isResizeE =  w - BORDER <= x && x < w;
            isResizeW = 0 <= x && x < BORDER;
                        
            int idx_x = isResizeW ? 0 : isResizeE ? 2 : 1;
            int idx_y = isResizeN ? 0 : isResizeS ? 2 : 1;
            
            int cursor = CURSORS[idx_y][idx_x];    
                
            IFrameService fServ = Services.get(IFrameService.class);
            fServ.getFrame().setCursor(new Cursor(cursor));
        }
    }
      
    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if (isResizeN || isResizeS || isResizeE || isResizeW)
        {
            IFrameService fServ = Services.get(IFrameService.class);
            JFrame frame = fServ.getFrame();
            
            sx = e.getXOnScreen();
            sy = e.getYOnScreen();
            
            fx = frame.getX();
            fy = frame.getY();
            fw = frame.getWidth();
            fh = frame.getHeight();
            
            resizing = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        resizing = false;
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {       
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        int cursor = CURSORS[1][1];    
        
        IFrameService fServ = Services.get(IFrameService.class);
        fServ.getFrame().setCursor(new Cursor(cursor));
    }
}
