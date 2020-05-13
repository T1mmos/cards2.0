package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_OnTcpConnectionClosed extends CommandBase
{
    private final UUID clientId;

    public C_OnTcpConnectionClosed(UUID clientId)
    {
        this.clientId = clientId;
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void preExecute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            if (state.getServerId() != null)
            {
                CommandBase cmd = new C_OnServerConnectionLost();
                context.schedule(cmd);
            }
        }
        else
        {
            C_RemovePlayer cmd = new C_RemovePlayer(clientId);
            context.schedule(cmd);
        }
    }

}
