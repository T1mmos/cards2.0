package gent.timdemey.cards.model.commands;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.Player;
import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.ICardGameCreatorService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_JoinGame extends CommandBase
{    
    String clientName;
    UUID clientId;
    
    C_JoinGame() 
    {
        this.clientName = null;
        this.clientId = null;
    }
    
    C_JoinGame(String clientName, UUID clientId)
    {
        this.clientName = clientName;
        this.clientId = clientId;
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
    	return true;
    }

    
    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        if (type == ContextType.Client)
        {
        	if (clientName == null)
        	{
        		clientName = state.getLocalName();        		
        	}
        	if (clientId == null)
        	{
        		clientId = state.getLocalId();
        	}
        	String json = CommandDtoMapper.toJson(this);
        	TCP_Connection tcpConnection = state.getTcpConnectionPool().getConnection(state.getServerId());
            tcpConnection.send(json);
        }
        else if (type == ContextType.Server)
        {
            TCP_Connection tcpConnection = getSourceTcpConnection();
            Services.get(ILogManager.class).log("Player %s (id %s) joining from %s", clientName, clientId, tcpConnection.getRemote());
            
            state.getTcpConnectionPool().bindUUID(clientId, tcpConnection);
            
            Player player = new Player(clientId, clientName);
            state.getPlayers().add(player);
            
            // send unicast to new client
            {
                CommandBase cmd_answer = new C_WelcomeClient(state.getLocalId(), state.getServerMessage(), state.getRemotePlayers());
                String json_answer = CommandDtoMapper.toJson(cmd_answer);
                state.getTcpConnectionPool().getConnection(clientId).send(json_answer);
            }
            
            // send update to already connected clients
            List<Player> others = state.getPlayers().getExcept(clientId);
            if (others.size() > 0)
            {
                CommandBase cmd_update = new C_HandlePlayerJoined(player);       
                String json_update = CommandDtoMapper.toJson(cmd_update);
                for (Player other : others)
                {
                    state.getTcpConnectionPool().getConnection(other.id).send(json_update);
                }
            }
            
            if (others.size() + 1 == Services.get(ICardPlugin.class).getPlayerCount())
            {
                // ready to kick off. Generate some cards for the current game type.
                ICardGameCreatorService creator = Services.get(ICardGameCreatorService.class);
                List<List<Card>> allCards = creator.getCards();                
                List<UUID> playerIds = state.getPlayers().getIds();
                Map<UUID, List<CardStack>> playerStacks = creator.createStacks(playerIds, allCards);
                              
                C_StartGame cmd = new C_StartGame(playerStacks);
                schedule(ContextType.Server, cmd);
            }
        }
        else
        {
            throw new IllegalStateException();
        }
    }
}
