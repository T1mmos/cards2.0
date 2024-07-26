package gent.timdemey.cards.model.entities.commands;

import java.util.List;
import java.util.UUID;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
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
        INetworkService networkService,
        UUID id, String clientName, UUID clientId)
    {
        super(contextService, id);
        
        this._Logger = logger;
        this._CommandFactory = commandFactory;
        this._StateFactory = stateFactory;
        this._NetworkService = networkService;
        this.clientName = clientName;
        this.clientId = clientId;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            if (state.getGameState() != GameState.Connected)
            {
                return CanExecuteResponse.no("GameState should be Connected but is: " + state.getGameState());
            }
        }
        else
        {
            if (state.getGameState() != GameState.Lobby)
            {
                return CanExecuteResponse.no("GameState should be Lobby but is: " + state.getGameState());
            }
        }
        
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {        
        if(type == ContextType.UI)
        {
            _NetworkService.send(state.getLocalId(), state.getServerId(), this, state.getTcpConnectionPool());
        }
        else if(type == ContextType.Server)
        {
            TCP_Connection tcpConnection = getSourceTcpConnection();
            _Logger.info("Player %s (id %s) joining from %s", clientName, clientId, tcpConnection.getRemote());

            state.getTcpConnectionPool().bindUUID(clientId, tcpConnection);

            if (state.getPlayers().getIds().contains(clientId) && !state.getLobbyAdminId().equals(clientId))
            {
                throw new IllegalStateException("This player already joined the lobby: " + clientId + " ( " + clientName + " )");
            }            
           
            Player player;
            if(!state.getLobbyAdminId().equals(clientId))
            {   
                player = _StateFactory.CreatePlayer(clientId, clientName);
                state.getPlayers().add(player);
            }
            else
            {
                player = state.getPlayers().get(clientId);
            }
            
            // send unicast to new client
            {
                CommandBase cmd_answer = _CommandFactory.CreateOnLobbyWelcome(clientId, state.getServerId(), state.getServerMessage(), state.getRemotePlayers(), state.getLobbyAdminId());
                _NetworkService.send(state.getLocalId(), clientId, cmd_answer, state.getTcpConnectionPool());
            }

            // send update to already connected clients
            List<UUID> updateIds = state.getPlayers().getExceptUUID(clientId);
            if(updateIds.size() > 0)
            {
                CommandBase cmd_update = _CommandFactory.CreateOnLobbyPlayerJoined(player);
                _NetworkService.broadcast(state.getLocalId(), updateIds, cmd_update, state.getTcpConnectionPool());
            }            
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("clientName", clientName) + Debug.getKeyValue("clientId", clientId);
    }
}
