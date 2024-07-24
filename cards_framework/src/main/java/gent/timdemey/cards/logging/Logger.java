package gent.timdemey.cards.logging;



public class Logger
{
    private final ILogManager _LogManager;
    
    private Logger(ILogManager logManager)
    {
        this._LogManager = logManager;
    }
    
    public void debug(Object msg)
    {
        log(LogLevel.DEBUG, msg);
    }

    public void debug(String msg, Object... params)
    {
        log(LogLevel.DEBUG, msg, params);
    }
    
    public void trace(Object msg)
    {
        log(LogLevel.TRACE, msg);
    }

    public void trace(String msg, Object... params)
    {
        log(LogLevel.TRACE, msg, params);
    }
    
    public void info(Object msg)
    {
        log(LogLevel.INFO, msg);
    }

    public void info(String msg, Object... params)
    {
        log(LogLevel.INFO, msg, params);
    }
    
    public void warn(Object msg)
    {
        log(LogLevel.WARN, msg);
    }

    public void warn(String msg, Object... params)
    {
        log(LogLevel.WARN, msg, params);
    }
    
    public void error(Object msg)
    {
        log(LogLevel.ERROR, msg);
    }

    public void error(String msg, Object... params)
    {
        log(LogLevel.ERROR, msg, params);
    }
    
    public void error(Throwable ex)
    {
        _LogManager.log(ex);
    }
    
    public void error(String msg, Throwable ex)
    {
        _LogManager.log(msg, ex);
    }
        
    private void log(LogLevel lvl, Object msg)
    {
        _LogManager.log(lvl, msg);
    }

    private void log(LogLevel lvl, String msg, Object... params)
    {
        _LogManager.log(lvl, msg, params);
    }

}
