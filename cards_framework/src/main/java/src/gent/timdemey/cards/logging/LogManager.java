package gent.timdemey.cards.logging;

public class LogManager implements ILogManager
{
    private LogLevel logLevel = LogLevel.DEBUG;

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
        String formatted = String.format("%-7s %-30s :: %s", lvl.name(), thrname, msg);
        System.out.println(formatted);
    }

    public void setLogLevel(LogLevel lvl)
    {
        this.logLevel = lvl;
    }
}
