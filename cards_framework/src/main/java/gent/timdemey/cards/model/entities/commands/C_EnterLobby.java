package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_EnterLobby;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.INetworkService;
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
    private final INetworkService _NetworkService;
    private final StateFactory _StateFactory;
    private final CommandFactory _CommandFactory;

    public C_EnterLobby(
        IContextService contextService, 
        Logger logger, 
        CommandFactory commandFactory,
        StateFactory stateFactory,
        State state,
        INetworkService networkService,
        P_EnterLobby parameters)
    {
        super(contextService, state, parameters);
        
        this._Logger = logger;
        this._CommandFactory = commandFactory;
        this._StateFactory = stateFactory;
        this._NetworkService = networkService;
        this.clientName = parameters.clientName;
        this.clientId = parameters.clientId;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        if (type == ContextType.UI)
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
    protected void execute(Context context, ContextType type)
    {        
        if(type == ContextType.UI)
        {
          
            _NetworkService.send(_State.getLocalId(), _State.getServerId(), this, _State.getTcpConnectionPool());
        }
        else if(type == ContextType.Server)
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
                _NetworkService.send(_State.getLocalId(), clientId, cmd_answer, _State.getTcpConnectionPool());
            }

            // send update to already connected clients
            List<UUID> updateIds = _State.getPlayers().getExceptUUID(clientId);
            if(updateIds.size() > 0)
            {
                CommandBase cmd_update = _CommandFactory.CreateOnLobbyPlayerJoined(player);
                _NetworkService.broadcast(_State.getLocalId(), updateIds, cmd_update, _State.getTcpConnectionPool());
            }            
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("clientName", clientName) + Debug.getKeyValue("clientId", clientId);
    }
}
