package gent.timdemey.cards.services.context;

import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.C_Accept;
import gent.timdemey.cards.model.entities.commands.C_DropPlayer;
import gent.timdemey.cards.model.entities.commands.C_Reject;
import gent.timdemey.cards.model.entities.commands.C_StartServer;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandHistory;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.netcode.CommandSchedulingTcpConnectionListener;
import gent.timdemey.cards.netcode.TCP_Connection;
import gent.timdemey.cards.netcode.TCP_ConnectionPool;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.INetworkService;
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
        Logger.info("Processing command '%s'", command.getName());

        CommandHistory cmdHist = state.getCommandHistory();

        // a command cannot be executed twice
        if (cmdHist != null && cmdHist.containsAccepted(command))
        {
            Logger.warn("This command was already executed: '%s'; ignoring", command.getName());
            return;
        }

        boolean executable = command.canExecute(state);
        boolean syncable = command.isSyncable();

        // syncable commands are commands that are executed clientside,
        // but can be rejected serverside e.g. when another player was earlier
        // but due to network delay the client wasn't yet aware.
        if (executable)
        {
            // server side we add all commands to the history, because this way
            // we can detect double transmits and correct programming errors.
            // it isn't strictly necessary because the server state is always
            // correct and does never need to rollback commands.
            if (command instanceof C_StartServer)
            {
                state.setCommandHistory(new CommandHistory(true));
            }
            state.getCommandHistory().addAccepted(command, state);

            // in case of syncable commands, we send a response to the source so
            // the client can mark the command as accepted in its history
            if (syncable)
            {
                C_Accept acceptCmd = new C_Accept(command.id);
                INetworkService ns = Services.get(INetworkService.class);
                ns.send(state.getLocalId(), command.getSourceId(), acceptCmd, state.getTcpConnectionPool());
            }
        }
        else
        {
            if (syncable)
            {
                Logger.info("\"Can't execute syncable command: '%s'. Responding with a C_Reject.", command.getName());

                C_Reject rejectCmd = new C_Reject(command.id);
                ISerializationService serServ = Services.get(ISerializationService.class);
                String answer = serServ.getCommandDtoMapper().toJson(rejectCmd);
                state.getTcpConnectionPool().getConnection(command.getSourceId()).send(answer);
            }
            else
            {
                Logger.error("Can't execute non-syncable command: '%s'.", command.getName());
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
