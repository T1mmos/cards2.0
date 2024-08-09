package gent.timdemey.cards.server;

import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.admin.P_StartServer;
import gent.timdemey.cards.services.context.ICommandExecutor;

/**
 *
 * @author Timmos
 */
public class StartServer
{
    private final ICommandExecutor _CommandExecutor;
    private final CommandFactory _CommandFactory;
    
    public StartServer (ICommandExecutor commandExecutor, CommandFactory commandFactory)
    {
        this._CommandExecutor = commandExecutor;
        this._CommandFactory = commandFactory;
    }
    
    public void startServer(P_StartServer parameters)
    {                   
        _CommandExecutor.schedule(_CommandFactory.CreateStartServer(parameters));
    }
}
