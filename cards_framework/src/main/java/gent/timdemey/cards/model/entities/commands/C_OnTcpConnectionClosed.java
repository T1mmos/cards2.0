package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_OnTcpConnectionClosed;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.services.context.ContextType;

public class C_OnTcpConnectionClosed extends CommandBase
{
    private final UUID connectionId;
    private final boolean local;
    private final CommandFactory _CommandFactory;

    public C_OnTcpConnectionClosed(
        Container container,
        CommandFactory commandFactory,
        P_OnTcpConnectionClosed parameters)
    {
        super(container, parameters);
        
        this._CommandFactory = commandFactory;
        
        this.connectionId = parameters.connectionId;
        this.local = parameters.local;
    }
    
    @Override
    public CanExecuteResponse canExecute()
    {
        GameState gameState = _State.getGameState();
        boolean expected = gameState == GameState.Disconnected;
        if (local && !expected)
        {
            return CanExecuteResponse.error("When the connection is closed by the local party, TcpOnConnectionClosed must be expected");
        }
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        if (_ContextType == ContextType.UI)
        {
            GameState gameState = _State.getGameState();
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
            run(cmd);
        }
    }

}
