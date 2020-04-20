package gent.timdemey.cards.services.context;

import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.entities.commands.C_Accept;
import gent.timdemey.cards.model.entities.commands.C_DropPlayer;
import gent.timdemey.cards.model.entities.commands.C_Reject;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.netcode.CommandSchedulingTcpConnectionListener;
import gent.timdemey.cards.netcode.TCP_Connection;
import gent.timdemey.cards.netcode.TCP_ConnectionPool;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.ISerializationService;

class ServerCommandExecutor extends CommandExecutorBase
{
    public ServerCommandExecutor()
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
            
            if (command.isSyncable())
            {
                C_Accept acceptCmd = new C_Accept(command.id);                
                ISerializationService serServ = Services.get(ISerializationService.class);
                String answer = serServ.getCommandDtoMapper().toJson(acceptCmd);
                state.getTcpConnectionPool().getConnection(command.getSourceId()).send(answer);
            }
            
        }
        else
        {
            Services.get(ILogManager.class).log("Can't execute this command: '" + command.getClass().getSimpleName() + "'");
            if (command.isSyncable())
            {
                C_Reject rejectCmd = new C_Reject(command.id);                
                ISerializationService serServ = Services.get(ISerializationService.class);
                String answer = serServ.getCommandDtoMapper().toJson(rejectCmd);
                state.getTcpConnectionPool().getConnection(command.getSourceId()).send(answer);
            }
        }

    }

    private class ConnListener extends CommandSchedulingTcpConnectionListener
    {
        public ConnListener()
        {
            super(ContextType.Server);
        }

        @Override
        public void onTcpConnectionAdded(TCP_Connection connection, TCP_ConnectionPool connectionPool)
        {
            super.onTcpConnectionAdded(connection, connectionPool);

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
