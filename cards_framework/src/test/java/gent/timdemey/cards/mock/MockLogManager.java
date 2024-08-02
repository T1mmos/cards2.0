package gent.timdemey.cards.mock;

import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.logging.LogLevel;

public class MockLogManager implements ILogManager
{

    @Override
    public void log(LogLevel lvl, Object msg)
    {
        log(lvl, msg.toString());
    }

    @Override
    public void log(LogLevel lvl, String msg, Object... params)
    {
        String msgstr = String.format(msg, params);
        log(lvl, msgstr);
    }

    @Override
    public void log(Throwable ex)
    {
        ex.printStackTrace(System.err);
    }

    @Override
    public void log(String msg, Throwable ex)
    {
        log(LogLevel.ERROR, msg);
        log(ex);
    }

    @Override
    public void setLogLevel(LogLevel lvl)
    {
        // we log everything in the mocks
    }
    
    private void log(LogLevel lvl, String msg)
    {
        String format = "MockLogManager :: %10s :: %s";
        String msgstr = String.format(format, lvl, msg);
        System.out.println(msgstr);
    }

}
