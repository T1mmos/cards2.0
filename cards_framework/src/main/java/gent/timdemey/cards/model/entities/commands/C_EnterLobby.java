package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import java.util.List;
import java.util.UUID;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_EnterLobby;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

/**
 * Unicast sent to the server upon attempting to join a lobby.
 * @author Tim
 *
 */
public class C_EnterLobby extends CommandBase
{
    public final String clientName;
    public final UUID clientId;
    private final Logger _Logger;
    private final StateFactory _StateFactory;
    private final CommandFactory _CommandFactory;

    public C_EnterLobby(
        Container container,
        Logger logger, 
        CommandFactory commandFactory,
        StateFactory stateFactory,
        P_EnterLobby parameters)
    {
        super(container, parameters);
        
        this._Logger = logger;
        this._CommandFactory = commandFactory;
        this._StateFactory = stateFactory;
        this.clientName = parameters.clientName;
        this.clientId = parameters.clientId;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        if (_ContextType == ContextType.UI)
        {
            if (_State.getGameState() != GameState.Connected)
            {
                return CanExecuteResponse.no("GameState should be Connected but is: " + _State.getGameState());
            }
        }
        else
        {
            if (_State.getGameState() != GameState.Lobby)
            {
                return CanExecuteResponse.no("GameState should be Lobby but is: " + _State.getGameState());
            }
        }
        
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {        
        if(_ContextType == ContextType.UI)
        {          
            send(_State.getServerId(), this);
        }
        else if(_ContextType == ContextType.Server)
        {
            TCP_Connection tcpConnection = getSourceTcpConnection();
            _Logger.info("Player %s (id %s) joining from %s", clientName, clientId, tcpConnection.getRemote());

            _State.getTcpConnectionPool().bindUUID(clientId, tcpConnection);

            if (_State.getPlayers().getIds().contains(clientId) && !_State.getLobbyAdminId().equals(clientId))
            {
                throw new IllegalStateException("This player already joined the lobby: " + clientId + " ( " + clientName + " )");
            }            
           
            Player player;
            if(!_State.getLobbyAdminId().equals(clientId))
            {   
                player = _StateFactory.CreatePlayer(clientId, clientName);
                _State.getPlayers().add(player);
            }
            else
            {
                player = _State.getPlayers().get(clientId);
            }
            
            // send unicast to new client
            {
                CommandBase cmd_answer = _CommandFactory.CreateOnLobbyWelcome(clientId, _State.getServerId(), _State.getServerMessage(), _State.getRemotePlayers(), _State.getLobbyAdminId());
                
                send(clientId, cmd_answer);
            }

            // send update to already connected clients
            List<UUID> updateIds = _State.getPlayers().getExceptUUID(clientId);
            if(updateIds.size() > 0)
            {
                CommandBase cmd_update = _CommandFactory.CreateOnLobbyPlayerJoined(player);
                send(updateIds, cmd_update);
            }            
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("clientName", clientName) + Debug.getKeyValue("clientId", clientId);
    }
}
