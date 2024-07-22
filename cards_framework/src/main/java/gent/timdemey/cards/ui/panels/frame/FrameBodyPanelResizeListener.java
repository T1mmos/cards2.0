package gent.timdemey.cards.ui.panels.frame;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;

class FrameBodyPanelResizeListener implements ComponentListener
{
    // relayout time window for a resize event. if a new resize event enters
    // in the current time window, only the last request is honored after the
    // initial time window is over. This prevents that all resize events
    // enforce a relayout, but it is still responsive towards a user if the
    // time window is set low enough.
    private static final int MS_WAIT_RELAYOUT = 30;
    
    // scale time window. additional amount of time to wait after the last
    // resize event in order to start the scale operation (which makes
    // nicely scaled images of all JScalableImages on the screen). This is
    // a rather heavy image processing operation so the delay must be high enough so
    // that the operation isn't executed all the time during resizing the window,
    // but also not too high so that a user doesn't see blocked/blurry quickly
    // scaled images for too long.
    private static final int MS_WAIT_SCALE = 300;

    private Timer timer;
    private TimerTask relayoutTask;
    private TimerTask rescaleTask;

    private long msLastRelayout = 0;
    private final IPanelService _PanelService;
    private final IFrameService _FrameService;

    public FrameBodyPanelResizeListener (
            IFrameService frameService,
            IPanelService panelService)
    {
        this._FrameService = frameService;
        this._PanelService = panelService;
    }
            
    
    @Override
    public void componentShown(ComponentEvent e)
    {

    }

    @Override
    public void componentResized(ComponentEvent e)
    {
        if (timer == null)
        {
            timer = new Timer("Resize Rescale Timer", true);
        }

        long time = System.currentTimeMillis();
        long diff = time - msLastRelayout;
        long wait_relayout = Math.max(MS_WAIT_RELAYOUT - diff, 0);
        
        if (relayoutTask != null)
        {
            relayoutTask.cancel();
            relayoutTask = null;
        }
        
        relayoutTask = new TimerTask()
        {
            @Override
            public void run()
            {
                relayout();
            }
        };
        timer.schedule(relayoutTask, wait_relayout);
        
        if (rescaleTask != null)
        {
            rescaleTask.cancel();
        }

        rescaleTask = new TimerTask()
        {
            @Override
            public void run()
            {
                msLastRelayout = System.currentTimeMillis();
                rescale();                
            }
        };

        timer.schedule(rescaleTask, wait_relayout + MS_WAIT_SCALE);
    }
    
    private void relayout()
    {  
        
        SwingUtilities.invokeLater(() -> 
        {
            _FrameService.updatePositionService();
            _PanelService.positionComponents();
        });
    }
    
    private void rescale()
    {
        SwingUtilities.invokeLater(() -> _PanelService.rescaleResourcesAsync(null));
    }
    
    @Override
    public void componentMoved(ComponentEvent e)
    {
    }

    @Override
    public void componentHidden(ComponentEvent e)
    {
    }
}
