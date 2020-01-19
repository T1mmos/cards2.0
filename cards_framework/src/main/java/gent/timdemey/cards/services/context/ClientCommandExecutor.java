package gent.timdemey.cards.services.context;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.state.State;

/**
 * Asynchronously processes commands. This is where all commands, from UI and
 * from the server, are processed. It is also here where commands are validated
 * and where rollback actions are initiated if necessary.
 * <p>
 * Adding commands will never block and is thread safe as well.
 * 
 * @author Timmos
 */
class ClientCommandExecutor extends CommandExecutorBase
{
    public ClientCommandExecutor()
    {
        super(ContextType.Client);
    }

    /**
     * Actually executes a command, which alters the game state, detects invalidity,
     * sends updates to the server, ...
     * 
     * @param command
     */
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
            // todo
        }
    }

    @Override
    public CommandHistory getCommandHistory()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
