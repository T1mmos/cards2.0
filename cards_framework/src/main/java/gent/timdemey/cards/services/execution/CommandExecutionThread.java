package gent.timdemey.cards.services.execution;

import gent.timdemey.cards.services.context.ContextType;

public class CommandExecutionThread extends Thread
{
    public final ContextType contextType;
    
    CommandExecutionThread(ContextType contextType)
    {
        super("CommandExecutionThread-" + contextType.toString());
        this.contextType = contextType;
    }
}
