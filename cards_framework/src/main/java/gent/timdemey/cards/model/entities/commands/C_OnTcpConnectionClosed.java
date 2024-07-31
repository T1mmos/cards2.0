package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_OnTcpConnectionClosed;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;

public class C_OnTcpConnectionClosed extends CommandBase
{
    private final UUID connectionId;
    private final boolean local;
    private final CommandFactory _CommandFactory;

    public C_OnTcpConnectionClosed(
        IContextService contextService, 
        CommandFactory commandFactory,
        P_OnTcpConnectionClosed parameters)
    {
        super(contextService, parameters);
        
        this._CommandFactory = commandFactory;
        
        this.connectionId = parameters.connectionId;
        this.local = parameters.local;
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        GameState gameState = state.getGameState();
        boolean expected = gameState == GameState.Disconnected;
        if (local && !expected)
        {
            return CanExecuteResponse.error("When the connection is closed by the local party, TcpOnConnectionClosed must be expected");
        }
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            GameState gameState = state.getGameState();
            boolean expected = gameState == GameState.Disconnected;
            
            if (!expected)
            {
                C_Disconnect cmd_disconnect =_CommandFactory.CreateDisconnect(DisconnectReason.ConnectionLost);
                run(cmd_disconnect);
            }
            // else: expected because C_Disconnect has already run.
        }
        else
        {
            C_RemovePlayer cmd = _CommandFactory.CreateRemovePlayer(connectionId);
            context.schedule(cmd);
        }
    }

}
