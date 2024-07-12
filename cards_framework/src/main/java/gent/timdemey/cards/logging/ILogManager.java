package gent.timdemey.cards.logging;

public interface ILogManager
{
    public void log(LogLevel lvl, Object msg);

    public void log(LogLevel lvl, String msg, Object... params);

    public void log(Throwable ex);
    
    public void log(String msg, Throwable ex);
    
    public void setLogLevel(LogLevel lvl);
}
