package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.readonlymodel.CommandType;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_Disconnect extends CommandBase
{
    C_Disconnect() 
    {
    }

    @Override
    public void execute(Context context, ContextType type, State state) 
    {
        if (type == ContextType.Client)
        {
            getProcessorClient().connPool.closeAllConnections();
            
            getThreadContext().removeRemotes();
            getThreadContext().setServerId(null);
        }
        else if (type == ContextType.UI)
        {
            getThreadContext().removeRemotes();
            getThreadContext().setServerId(null);
            
            reschedule(ContextType.Client);
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return type == ContextType.Client || type == ContextType.UI; 
    }
}
