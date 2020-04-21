package gent.timdemey.cards.logging;

public class LogManager implements ILogManager
{
    @Override
    public void log(Object msg)
    {
        String thrname = Thread.currentThread().getName();
        String formatted = String.format("%-30s :: %s", thrname, msg);
        System.out.println(formatted);
    }

    @Override
    public void log(String msg, Object... params)
    {
        log(String.format(msg, params));
    }

    @Override
    public void log(Throwable ex)
    {
        log("!!! EXCEPTION !!! Caught on thread " + Thread.currentThread().getName());
        ex.printStackTrace(System.out);
    }
}
