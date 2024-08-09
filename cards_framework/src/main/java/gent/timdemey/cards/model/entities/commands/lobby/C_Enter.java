package gent.timdemey.cards.model.entities.commands.lobby;

import gent.timdemey.cards.di.Container;
import java.util.List;
import java.util.UUID;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
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
public class C_Enter extends CommandBase<P_Enter>
{
    public final String clientName;
    private final Logger _Logger;
    private final StateFactory _StateFactory;
    private final CommandFactory _CommandFactory;

    public C_Enter(
        Container container,
        Logger logger, 
        CommandFactory commandFactory,
        StateFactory stateFactory,
        P_Enter parameters)
    {
        super(container, parameters);
        
        this._Logger = logger;
        this._CommandFactory = commandFactory;
        this._StateFactory = stateFactory;
        this.clientName = parameters.clientName;
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
            send(_State.getServer().id, this);
        }
        else if(_ContextType == ContextType.Server)
        {
            TCP_Connection tcpConnection = getSourceTcpConnection();
            _Logger.info("Player %s (id %s) joining from %s", clientName, creatorId, tcpConnection.getRemote());

            _State.getTcpConnectionPool().bindUUID(creatorId, tcpConnection);

            if (_State.getPlayers().getIds().contains(creatorId) && !_State.getLobbyAdminId().equals(creatorId))
            {
                throw new IllegalStateException("This player already joined the lobby: " + creatorId + " ( " + clientName + " )");
            }            
           
            Player player;
            if(!_State.getLobbyAdminId().equals(creatorId))
            {   
                player = _StateFactory.CreatePlayer(creatorId, clientName);
                _State.getPlayers().add(player);
            }
            else
            {
                player = _State.getPlayers().get(creatorId);
            }
            
            // send unicast to new client
            {
                CommandBase cmd_answer = _CommandFactory.CreateOnLobbyWelcome(creatorId, _State.getServer().id, _State.getServerMessage(), _State.getRemotePlayers(), _State.getLobbyAdminId());
                
                send(creatorId, cmd_answer);
            }

            // send update to already connected clients
            List<UUID> updateIds = _State.getPlayers().getExceptUUID(creatorId);
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
        return Debug.getKeyValue("clientName", clientName) + Debug.getKeyValue("clientId", creatorId);
    }
}
