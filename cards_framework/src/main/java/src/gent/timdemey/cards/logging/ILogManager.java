package gent.timdemey.cards.logging;

public interface ILogManager
{

    public void log(Object msg);

    public void log(String msg, Object... params);

    public void log(Throwable ex);
}
