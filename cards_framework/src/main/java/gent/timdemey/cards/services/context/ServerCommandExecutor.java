package gent.timdemey.cards.services.context;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandHistory;
import gent.timdemey.cards.model.entities.commands.CommandType;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.contract.ExecutionState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.netcode.ws.GameServer;

class ServerCommandExecutor extends CommandExecutorBase
{
    public ServerCommandExecutor()
    {
        super(ContextType.Server);
    }

    @Override
    protected void execute(CommandBase command, State state)
    {
        Logger.info("Processing command '%s', id=%s", command.getName(), command.id);

        CommandHistory cmdHist = state.getCommandHistory();

        // a command cannot be executed twice
        if (cmdHist != null && cmdHist.containsAccepted(command))
        {
            Logger.warn("This command was already executed: '%s'; ignoring", command.getName());
            return;
        }

        CanExecuteResponse resp = command.canExecute(state);
        CommandType cmdType = command.getCommandType();

        // syncable commands are commands that are executed clientside,
        // but can be rejected serverside e.g. when another player was earlier
        // but due to network delay the client wasn't yet aware.
        if (resp.execState == ExecutionState.Yes)
        {
            if (cmdType == CommandType.TRACKED || cmdType == CommandType.SYNCED)
            {
                state.getCommandHistory().addAccepted(command, state);

                // in case of synced commands, all players are informed through a numbered
                // MoveAccepted event so the source can mark the command as accepted in its history
                // and the other players apply it
                if (cmdType == CommandType.SYNCED)
                {
                    GameServer gameServer = state.getGameServer();
                    if (gameServer != null)
                    {
                        gameServer.onMoveAccepted(state, command);
                    }
                }
            }
            else if (cmdType == CommandType.DEFAULT)
            {
                command.execute(state);
            }
        }
        else if (resp.execState == ExecutionState.No)
        {
            if (cmdType == CommandType.SYNCED)
            {
                Logger.info("Can't execute syncable command: '%s'. Broadcasting a MoveRejected. Reason: %s", command.getName(), resp.reason);

                GameServer gameServer = state.getGameServer();
                if (gameServer != null)
                {
                    gameServer.onMoveRejected(state, command, resp.reason);
                }
            }
            else
            {
                Logger.error("Can't execute non-syncable command: '%s'. Reason: %s", command.getName(), resp.reason);
            }
        }
        else
        {
            Logger.error("Can't execute command '%s' because of state error. Reason: %s", command.getName(), resp.reason);
        }
    }
}
