package gent.timdemey.cards.logging;

import gent.timdemey.cards.Services;

public class Logger
{
    private static final ILogManager logMan = Services.get(ILogManager.class);
    
    private Logger()
    {
    }
    
    public static void debug(Object msg)
    {
        log(LogLevel.DEBUG, msg);
    }

    public static void debug(String msg, Object... params)
    {
        log(LogLevel.DEBUG, msg, params);
    }
    
    public static void trace(Object msg)
    {
        log(LogLevel.TRACE, msg);
    }

    public static void trace(String msg, Object... params)
    {
        log(LogLevel.TRACE, msg, params);
    }
    
    public static void info(Object msg)
    {
        log(LogLevel.INFO, msg);
    }

    public static void info(String msg, Object... params)
    {
        log(LogLevel.INFO, msg, params);
    }
    
    public static void warn(Object msg)
    {
        log(LogLevel.WARN, msg);
    }

    public static void warn(String msg, Object... params)
    {
        log(LogLevel.WARN, msg, params);
    }
    
    public static void error(Object msg)
    {
        log(LogLevel.ERROR, msg);
    }

    public static void error(String msg, Object... params)
    {
        log(LogLevel.ERROR, msg, params);
    }
    
    public static void error(Throwable ex)
    {
        logMan.log(ex);
    }
    
    public static void error(String msg, Throwable ex)
    {
        logMan.log(msg, ex);
    }
    
    private static void log(LogLevel lvl, Object msg)
    {
        logMan.log(lvl, msg);
    }

    private static void log(LogLevel lvl, String msg, Object... params)
    {
        logMan.log(lvl, msg, params);
    }

}
