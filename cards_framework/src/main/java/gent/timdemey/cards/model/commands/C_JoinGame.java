package gent.timdemey.cards.model.commands;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.other.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.ICardGameCreationService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_JoinGame extends CommandBase
{
    public String clientName;
    public UUID clientId;
    public UUID serverId;

    public C_JoinGame(UUID serverId)
    {
        this.clientName = null;
        this.clientId = null;
        this.serverId = serverId;
    }

    public C_JoinGame(UUID serverId, String clientName, UUID clientId)
    {
        this.clientName = clientName;
        this.clientId = clientId;
        this.serverId = serverId;
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return state.getCardGame() == null;
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
            
            state.setServerId(serverId);
            String json = CommandDtoMapper.toJson(this);
            TCP_Connection tcpConnection = state.getTcpConnectionPool().getConnection(state.getServerId());
            tcpConnection.send(json);
        }
        else if (type == ContextType.Server)
        {
            TCP_Connection tcpConnection = getSourceTcpConnection();
            Services.get(ILogManager.class).log("Player %s (id %s) joining from %s", clientName, clientId,
                    tcpConnection.getRemote());

            state.getTcpConnectionPool().bindUUID(clientId, tcpConnection);

            Player player = new Player(clientId, clientName);
            state.getPlayers().add(player);

            // send unicast to new client
            {
                CommandBase cmd_answer = new C_WelcomeClient(state.getLocalId(), state.getServerMessage(),
                        state.getRemotePlayers());
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
                ICardGameCreationService creator = Services.get(ICardGameCreationService.class);
                List<List<Card>> allCards = creator.getCards();
                List<UUID> playerIds = state.getPlayers().getIds();
                Map<UUID, List<CardStack>> playerStacks = creator.createStacks(playerIds, allCards);

                UUID cardGameId = UUID.randomUUID();
                C_StartGame cmd = new C_StartGame(cardGameId, playerStacks);
                schedule(ContextType.Server, cmd);
            }
        }
        else
        {
            throw new IllegalStateException();
        }
    }
}
