package gent.timdemey.cards.services.execution;

import java.util.UUID;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.commands.C_HandleConnectionLoss;
import gent.timdemey.cards.model.commands.C_JoinGame;
import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.ITcpConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceRequester;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;

/**
 * Asynchronously processes commands. This is where all commands, from UI
 * and from the server, are processed. It is also here where commands are
 * validated and where rollback actions are initiated if necessary.
 * <p>Adding commands will never block and is thread safe as well.
 * @author Timmos
 */
public class ClientCommandExecutionService extends CommandExecutionServiceBase 
{
    public ClientCommandExecutionService()
	{
		super(ContextType.Client);
	}  
            
    /**
     * Actually executes a command, which alters the game state, detects 
     * invalidity, sends updates to the server, ...
     * @param command
     */
    @Override
    protected void execute(CommandBase command, State state)
    {
        Services.get(ILogManager.class).log("Processing command '" + command.getClass().getSimpleName() +"'");
        
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
    public void setExecutionListener(IExecutionListener executionListener)
    {
        throw new UnsupportedOperationException("Currently executionlisteners are not supported in the Client execution service.");
    }
}
