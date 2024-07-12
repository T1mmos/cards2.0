package gent.timdemey.cards.services.context;

public class CommandExecutionThread extends Thread
{
    public final ContextType contextType;

    CommandExecutionThread(Runnable r, ContextType contextType)
    {
        super(r, contextType.toString() + " :: CommandExecution");
        this.setDaemon(true);
        this.contextType = contextType;
    }
}
