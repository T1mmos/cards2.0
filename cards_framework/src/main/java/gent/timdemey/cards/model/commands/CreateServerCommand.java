package gent.timdemey.cards.model.commands;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class CreateServerCommand extends CommandBase
{
    private final String serverName;
    private final String serverMessage;
    private final UUID serverId;

    public CreateServerCommand(String serverName, String serverMessage, UUID serverId)
    {
        this.serverName = serverName;
        this.serverMessage = serverMessage;
        this.serverId = serverId;
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            Services.get(IContextService.class).initialize(ContextType.Server);

            reschedule(ContextType.Server);
        }
        else if (type == ContextType.Server)
        {
            state.setLocalName(serverName);
            state.setServerMessage(serverMessage);
            state.setServerId(serverId);
            state.setLocalId(serverId);

            // todo connection pool etc

        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }
}
