package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.game.C_RemovePlayer;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.net.C_TCP_ClientDisconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.services.context.ContextType;

public class C_TCP_HandleClosed extends CommandBase<P_TCP_HandleClosed>
{
    private final UUID connectionId;
    private final boolean local;
    private final CommandFactory _CommandFactory;

    public C_TCP_HandleClosed(
        Container container,
        CommandFactory commandFactory,
        P_TCP_HandleClosed parameters)
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
                C_TCP_ClientDisconnect cmd_disconnect =_CommandFactory.CreateTCPClientDisconnect(DisconnectReason.ConnectionLost);
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
