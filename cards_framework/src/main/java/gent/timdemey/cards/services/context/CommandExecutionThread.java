package gent.timdemey.cards.services.context;

public class CommandExecutionThread extends Thread
{
    public final ContextType contextType;

    CommandExecutionThread(ContextType contextType)
    {
        super("CommandExecutionThread-" + contextType.toString());
        this.contextType = contextType;
    }
}
