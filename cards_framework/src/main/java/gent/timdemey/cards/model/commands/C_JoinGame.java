package gent.timdemey.cards.model.commands;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.commands.CommandEnvelope;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.readonlymodel.ACommand;
import gent.timdemey.cards.readonlymodel.ACommandPill;
import gent.timdemey.cards.readonlymodel.CommandType;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.services.ICardGameCreatorService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_JoinGame extends CommandBase
{    
    final String clientName;
    final UUID clientId;
    
    C_JoinGame(String clientName, UUID clientId) {
        this.clientName = clientName;
        this.clientId = clientId;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.Meta;
    }

    @Override
    public void execute() 
    {
        ContextType contextType = getContextType();
        if (contextType == ContextType.Client)
        {
            TCP_Connection tcpConnection = (TCP_Connection) getVolatileData();
            tcpConnection.send(Json.send(CommandEnvelope.createCommandEnvelope(this)));
        }
        else if (contextType == ContextType.Server)
        {
            TCP_Connection tcpConnection = (TCP_Connection) getVolatileData();
            Services.get(ILogManager.class).log("Player %s (id %s) joining from %s", clientName, clientId, tcpConnection.getRemote());
            
            getProcessorServer().srv_tcp_connpool.bindUUID(clientId, tcpConnection);
            Context context = getThreadContext();
            
            context.addPlayer(clientId, clientName);
            
            // send unicast to new client
            ICommand answer = new C_WelcomeClient( 
                    context.getLocalId(), 
                    context.getServerMessage(),
                    context.getParties());
            
            getProcessorServer().srv_tcp_connpool.getConnection(clientId).send(Json.send(CommandEnvelope.createCommandEnvelope(answer)));
            
            // send update to already connected clients
            List<ReadOnlyPlayer> others = context.getPlayersExcept(clientId);
            if (others.size() > 0)
            {
                ACommand updateCmd = new C_HandlePlayerJoined(context.getPlayer(clientId));       
                CommandEnvelope envelope = CommandEnvelope.createCommandEnvelope(updateCmd);
                String serializedEnv = envelope.serialize();
                for (ReadOnlyPlayer player : others)
                {
                    getProcessorServer().srv_tcp_connpool.getConnection(player.id).send(serializedEnv);
                }
            }
            
            if (others.size() + 1 == Services.get(ICardPlugin.class).getPlayerCount())
            {
                // ready to kick off. Generate some cards for the current game type.
                ICardGameCreatorService creator = Services.get(ICardGameCreatorService.class);
                List<List<ReadOnlyCard>> allCards = creator.getCards();
                List<UUID> playerIds = getThreadContext().getPlayerIds();
                Map<UUID, List<ReadOnlyCardStack>> playerStacks = creator.createStacks(playerIds, allCards);
                              
                C_StartGame cmd = new C_StartGame(playerStacks);
                cmd.schedule(ContextType.Server);
            }
        }
        else
        {
            throw new IllegalStateException();
        }
    }
}
