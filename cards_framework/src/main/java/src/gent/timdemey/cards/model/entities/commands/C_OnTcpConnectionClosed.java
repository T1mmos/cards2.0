package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_OnTcpConnectionClosed extends CommandBase
{
    private final UUID connectionId;
    private final boolean local;

    public C_OnTcpConnectionClosed(UUID connectionId, boolean local)
    {
        this.connectionId = connectionId;
        this.local = local;
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        if (local && state.getServerId() != null)
        {
            return CanExecuteResponse.error("When the connection is closed by the local party, State.ServerId must be null");
        }
        if (!local && state.getServerId() == null)
        {
            return CanExecuteResponse.error("When the connection is closed by the remote party, State.ServerId must not be null");
        }
        return CanExecuteResponse.yes();
    }

    @Override
    protected void preExecute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            if (!local)
            {
                CommandBase cmd = new C_OnServerConnectionLost();
                context.schedule(cmd);
            }
        }
        else
        {
            C_RemovePlayer cmd = new C_RemovePlayer(connectionId);
            context.schedule(cmd);
        }
    }

}
