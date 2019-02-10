package gent.timdemey.cards.entities;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;

class C_JoinGame extends ACommandPill  {
    static class CompactConverter extends ACommandSerializer<C_JoinGame>
    {
        @Override
        protected void writeCommand(SerializationContext<C_JoinGame> sc) {
            writeString(sc, PROPERTY_CLIENT_NAME, sc.src.clientName);
            writeString(sc, PROPERTY_CLIENT_ID, sc.src.clientId.toString());
        }

        @Override   
        protected C_JoinGame readCommand(DeserializationContext dc, MetaInfo metaInfo) {
            String clientName = readString(dc, PROPERTY_CLIENT_NAME);
            UUID clientId = UUID.fromString(readString(dc, PROPERTY_CLIENT_ID));
           
                       
            return new C_JoinGame(metaInfo, clientName, clientId);
        }        
    }
    
    final String clientName;
    final UUID clientId;
    
    C_JoinGame(MetaInfo info, String clientName, UUID clientId) {
        super(info);

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
            tcpConnection.send(Json.send(new CommandEnvelope(this)));
        }
        else if (contextType == ContextType.Server)
        {
            TCP_Connection tcpConnection = (TCP_Connection) getVolatileData();
            Services.get(ILogManager.class).log("Player %s (id %s) joining from %s", clientName, clientId, tcpConnection.getRemote());
            
            getProcessorServer().srv_tcp_connpool.bindUUID(clientId, tcpConnection);
            ContextFull context = getThreadContext();
            
            context.addPlayer(clientId, clientName);
            
            // send unicast to new client
            ICommand answer = new C_WelcomeClient(
                    new MetaInfo(0,0, context.getLocalId()), 
                    context.getLocalId(), 
                    context.getServerMessage(),
                    context.getParties());
            
            getProcessorServer().srv_tcp_connpool.getConnection(clientId).send(answer.serialize());
            
            // send update to already connected clients
            List<Player> others = context.getPlayersExcept(clientId);
            if (others.size() > 0)
            {
                ICommand update = new C_PlayerJoined(new MetaInfo(0, 0, context.getLocalId()), context.getPlayer(clientId));                
                for (Player player : others)
                {
                    getProcessorServer().srv_tcp_connpool.getConnection(player.id).send(update.serialize());
                }
            }
            
            if (others.size() + 1 == Services.get(ICardPlugin.class).getPlayerCount())
            {
                // ready to kick off. Generate some cards for the current game type.
                ICardGameCreator creator = Services.get(ICardGameCreator.class);
                List<List<E_Card>> allCards = creator.getCards();
                List<UUID> playerIds = getThreadContext().getPlayerIds();
                Map<UUID, List<E_CardStack>> playerStacks = creator.createStacks(playerIds, allCards);
                              
                C_StartGame cmd = new C_StartGame(new MetaInfo(0,0,getThreadContext().getServerId()), playerStacks);
                cmd.scheduleOn(ContextType.Server);
            }
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public void visitExecuted(IGameEventListener listener) {
        // TODO Auto-generated method stub
        
    }

}
