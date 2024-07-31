package gent.timdemey.cards.utils;

import java.util.function.Consumer;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.logging.Logger;

public class ThreadUtils
{
    public static void executeAndContinueOnUi(Logger logger, String threadName, Runnable threadCode, final Consumer<Async> uiCode)
    {        
        Runnable thrRun = () ->
        {
            boolean success = false;
            try 
            {
                threadCode.run();
                success = true;
            }
            finally
            {
                final boolean uiSuccess = success;
                SwingUtilities.invokeLater(() -> 
                {
                    try
                    {
                        Async async = new Async(uiSuccess);
                        uiCode.accept(async);
                    }
                    catch (Exception e)
                    {
                        logger.error(e);
                    }
                });
            }
        };
        Thread thr = new Thread(thrRun, threadName);
        thr.start();
    }
    
    public static void checkEDT()
    {
        if (!SwingUtilities.isEventDispatchThread())
        {
            throw new IllegalStateException("Expected to be on the Event Dispatching Thread, current thread=" + Thread.currentThread().getName());
        }
    }
}
