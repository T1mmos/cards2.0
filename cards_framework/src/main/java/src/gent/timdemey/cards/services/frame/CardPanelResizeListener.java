package gent.timdemey.cards.services.frame;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.interfaces.IPanelService;

class CardPanelResizeListener implements ComponentListener
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
    // scaled images
    // for too long.
    private static final int MS_WAIT_SCALE = 300;

    private Timer timer;
    private TimerTask relayoutTask;
    private TimerTask scaleTask;

    private long msLastRelayout = 0;

    @Override
    public void componentShown(ComponentEvent e)
    {

    }

    @Override
    public void componentResized(ComponentEvent e)
    {
        if (timer == null)
        {
            timer = new Timer("Resize Rescale Timer Thread");
        }

        if (relayoutTask != null)
        {
            relayoutTask.cancel();
            relayoutTask = null;
        }

        long time = System.currentTimeMillis();
        long diff = time - msLastRelayout;
        if (diff > MS_WAIT_RELAYOUT)
        {
            msLastRelayout = System.currentTimeMillis();
            SwingUtilities.invokeLater(() -> Services.get(IPanelService.class).relayout());
        }
        else
        {
            relayoutTask = new TimerTask()
            {

                @Override
                public void run()
                {
                    SwingUtilities.invokeLater(() -> Services.get(IPanelService.class).relayout());
                }
            };
            timer.schedule(relayoutTask, MS_WAIT_RELAYOUT);
        }

        if (scaleTask != null)
        {
            scaleTask.cancel();
        }

        scaleTask = new TimerTask()
        {

            @Override
            public void run()
            {
                doUpdateUI();                
            }
        };

        timer.schedule(scaleTask, MS_WAIT_RELAYOUT + MS_WAIT_SCALE);
    }
    
    private void doUpdateUI()
    {
        IPanelService gamePanelServ = Services.get(IPanelService.class);
        SwingUtilities.invokeLater(() -> gamePanelServ.rescaleResourcesAsync());
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
