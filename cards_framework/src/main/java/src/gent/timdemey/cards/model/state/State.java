package gent.timdemey.cards.model.state;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.commands.CommandHistory;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.entities.game.UDPServer;
import gent.timdemey.cards.netcode.TCP_ConnectionAccepter;
import gent.timdemey.cards.netcode.TCP_ConnectionPool;
import gent.timdemey.cards.netcode.UDP_ServiceAnnouncer;
import gent.timdemey.cards.netcode.UDP_ServiceRequester;

public class State extends EntityBase
{
    public static final Property<CardGame> CardGame = Property.of(State.class, CardGame.class, "CardGame");
    public static final Property<CommandHistory> CommandHistory = Property.of(State.class, CommandHistory.class, "CommandHistory");
    public static final Property<GameState> GameState = Property.of(State.class, GameState.class, "GameState");
    public static final Property<UUID> LocalId = Property.of(State.class, UUID.class, "LocalId");
    public static final Property<String> LocalName = Property.of(State.class, String.class, "LocalName");
    public static final Property<UUID> LobbyAdminId = Property.of(State.class, UUID.class, "LobbyAdminId");
    public static final Property<Player> Players = Property.of(State.class, Player.class, "Players");
    public static final Property<Server> Server = Property.of(State.class, Server.class, "Server");
    public static final Property<String> ServerMsg = Property.of(State.class, String.class, "ServerMsg");    
    public static final Property<UDPServer> UDPServers = Property.of(State.class, UDPServer.class, "UDPServers");

    private StateValueRef<CommandHistory> commandHistoryRef;

    // state lists
    private final EntityStateListRef<Player> playersRef;
    private final EntityStateListRef<UDPServer> serversRef;

    // state values
    private StateValueRef<CardGame> cardGameRef;
    private StateValueRef<GameState> gameStateRef;
    private StateValueRef<UUID> localIdRef;
    private StateValueRef<String> localNameRef;
    private StateValueRef<UUID> lobbyAdminId;
    private StateValueRef<Server> serverRef;
    private StateValueRef<String> serverMsgRef;

    // context specific
    private TCP_ConnectionAccepter tcpConnectionAccepter = null;
    private TCP_ConnectionPool tcpConnectionPool = null;
    private UDP_ServiceAnnouncer udpServiceAnnouncer = null;
    private UDP_ServiceRequester udpServiceRequester = null;

    public State()
    {
        cardGameRef = new StateValueRef<>(CardGame, id);
        gameStateRef = new StateValueRef<>(GameState, id, gent.timdemey.cards.model.entities.game.GameState.NotConnected);
        commandHistoryRef = new StateValueRef<>(CommandHistory, id);
        localIdRef = new StateValueRef<>(LocalId, id);
        localNameRef = new StateValueRef<>(LocalName, id);
        playersRef = new EntityStateListRef<>(Players, id, new ArrayList<>());
        lobbyAdminId = new StateValueRef<>(LobbyAdminId, id);
        serverRef = new StateValueRef<>(Server, id);
        serverMsgRef = new StateValueRef<>(ServerMsg, id);
        serversRef = new EntityStateListRef<>(UDPServers, id, new ArrayList<>());
    }

    public CardGame getCardGame()
    {
        return cardGameRef.get();
    }

    public void setCardGame(CardGame cardGame)
    {
        cardGameRef.set(cardGame);
    }

    public GameState getGameState()
    {
        return gameStateRef.get();
    }

    public void setGameState(GameState gameState)
    {
        gameStateRef.set(gameState);
    }

    public TCP_ConnectionAccepter getTcpConnectionAccepter()
    {
        return tcpConnectionAccepter;
    }

    public void setTcpConnectionAccepter(TCP_ConnectionAccepter tcpConnectionAccepter)
    {
        if(this.tcpConnectionAccepter != null)
        {
            try
            {
                this.tcpConnectionAccepter.stop();
            }
            catch (Exception ex)
            {
                Logger.error(ex);
            }
        }
        this.tcpConnectionAccepter = tcpConnectionAccepter;
    }

    public TCP_ConnectionPool getTcpConnectionPool()
    {
        return tcpConnectionPool;
    }

    public void setTcpConnectionPool(TCP_ConnectionPool tcpConnectionPool)
    {
        if(this.tcpConnectionPool != null)
        {
            try
            {
                this.tcpConnectionPool.closeAllConnections();
                this.tcpConnectionPool.stop();
            }
            catch (Exception ex)
            {
                Logger.error(ex);
            }
        }
        this.tcpConnectionPool = tcpConnectionPool;
    }

    public UDP_ServiceRequester getUdpServiceRequester()
    {
        return udpServiceRequester;
    }

    public void setUdpServiceRequester(UDP_ServiceRequester udpServiceRequester)
    {
        if(this.udpServiceRequester != null)
        {
            try
            {
                this.udpServiceRequester.stop();
            }
            catch (Exception ex)
            {
                Logger.error(ex);
            }
        }
        this.udpServiceRequester = udpServiceRequester;
    }

    public UDP_ServiceAnnouncer getUdpServiceAnnouncer()
    {
        return udpServiceAnnouncer;
    }

    public void setUdpServiceAnnouncer(UDP_ServiceAnnouncer udpServiceAnnouncer)
    {
        if(this.udpServiceAnnouncer != null)
        {
            try
            {
                this.udpServiceAnnouncer.stop();
            }
            catch (Exception ex)
            {
                Logger.error(ex);
            }
        }
        this.udpServiceAnnouncer = udpServiceAnnouncer;
    }

    public UUID getLobbyAdminId()
    {
        return lobbyAdminId.get();
    }

    public void setLobbyAdminId(UUID lobbyAdminId)
    {
        this.lobbyAdminId.set(lobbyAdminId);
    }

    public boolean isLocalId(UUID id)
    {
        return getLocalId().equals(id);
    }

    public UUID getLocalId()
    {
        return localIdRef.get();
    }

    public void setLocalId(UUID id)
    {
        localIdRef.set(id);
    }

    public String getLocalName()
    {
        return localNameRef.get();
    }

    public void setLocalName(String name)
    {
        localNameRef.set(name);
    }

    public EntityStateListRef<Player> getPlayers()
    {
        return playersRef;
    }

    public List<Player> getRemotePlayers()
    {
        return playersRef.getExcept(serverRef.get().id, localIdRef.get());
    }

    public List<UUID> getRemotePlayerIds()
    {
        return playersRef.getExceptUUID(serverRef.get().id, localIdRef.get());
    }

    public Server getServer()
    {
        return serverRef.get();
    }

    public void setServer(Server server)
    {
        serverRef.set(server);
    }

    public UUID getServerId()
    {
        Server server = getServer();
        if (server == null)
        {
            return null;
        }
        return server.id;
    }

    public EntityStateListRef<UDPServer> getUDPServers()
    {
        return serversRef;
    }

    public String getServerMessage()
    {
        return serverMsgRef.get();
    }

    public void setServerMessage(String serverMsg)
    {
        serverMsgRef.set(serverMsg);
    }

    public void setCommandHistory(CommandHistory commandHistory)
    {
        this.commandHistoryRef.set(commandHistory);
    }

    public CommandHistory getCommandHistory()
    {
        return this.commandHistoryRef.get();
    }

    @Override
    public final String toDebugString()
    {
        return "State";
    }
}
