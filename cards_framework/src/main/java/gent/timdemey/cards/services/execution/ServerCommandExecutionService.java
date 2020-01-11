package gent.timdemey.cards.services.execution;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.commands.C_Composite;
import gent.timdemey.cards.model.commands.C_DropPlayer;
import gent.timdemey.cards.model.commands.C_Move;
import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.CommandSchedulingTcpConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionAccepter;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;

public class ServerCommandExecutionService extends CommandExecutionServiceBase
{
    private TCP_ConnectionAccepter srv_tcp_accepter = null;

    public ServerCommandExecutionService()
    {
        super(ContextType.Server);
    }

    @Override
    protected void execute(CommandBase command, State state)
    {
        Services.get(ILogManager.class).log("Processing command '" + command.getClass().getSimpleName() + "'");
        if (command.canExecute(state))
        {
            command.execute(state);
        }
        else
        {
            if (command.getSourceId() == state.getServerId())
            {
                // wtf? 
            }
            // send reject command if command.SourceId != server
        }

    }

    private class ConnListener extends CommandSchedulingTcpConnectionListener
    {

        public ConnListener()
        {
            super(ContextType.Server);
        }

        @Override
        public void onTcpConnectionAdded(TCP_Connection connection)
        {
            super.onTcpConnectionAdded(connection);
            
            int requiredPlayerCount = Services.get(ICardPlugin.class).getPlayerCount();

        }

        @Override
        public void onTcpConnectionLocallyClosed(UUID id, TCP_Connection connection)
        {

        }

        @Override
        public void onTcpConnectionRemotelyClosed(UUID id, TCP_Connection connection)
        {
            if (id != null)
            {
                LimitedContext context = Services.get(IContextService.class).getContext(ContextType.Server);
                CommandBase cmd = new C_DropPlayer(id);
                context.schedule(cmd);
            }
        }

    }
}
