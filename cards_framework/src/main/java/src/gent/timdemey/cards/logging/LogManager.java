package gent.timdemey.cards.logging;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LogManager implements ILogManager
{
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss.SSS";
    
    private LogLevel logLevel = LogLevel.DEBUG;

    public LogManager (LogLevel logLevel)
    {
        this.logLevel = logLevel;
    }
    
    @Override
    public void log(LogLevel lvl, Object msg)
    {
        logAction(lvl, () ->
        {
            append(lvl, msg);
        });
    }

    @Override
    public void log(LogLevel lvl, String msg, Object... params)
    {
        logAction(lvl, () ->
        {
            append(lvl, String.format(msg, params));
        });
    }

    @Override
    public void log(Throwable ex)
    {
        logAction(LogLevel.ERROR, () ->
        {
            append(LogLevel.ERROR, "!!! EXCEPTION !!! Caught on thread " + Thread.currentThread().getName());
            ex.printStackTrace(System.out);
        });
    }

    @Override
    public void log(String msg, Throwable ex)
    {
        logAction(LogLevel.ERROR, () ->
        {
            append(LogLevel.ERROR, "!!! EXCEPTION !!! Caught on thread " + Thread.currentThread().getName());
            append(LogLevel.ERROR, msg);
            ex.printStackTrace(System.out);
        });
    }

    private void logAction(LogLevel lvl, Runnable logcode)
    {
        if (logLevel.compareTo(lvl) > 0)
        {
            return;
        }

        logcode.run();
    }

    private void append(LogLevel lvl, Object msg)
    {
        String thrname = Thread.currentThread().getName();
        String formatted = String.format("%s %-7s %-25s :: %s", now(), lvl.name(), thrname, msg);
        System.out.println(formatted);
    }

    public final void setLogLevel(LogLevel lvl)
    {
        this.logLevel = lvl;
    }
    
    private static String now() 
    {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }
}
