package gent.timdemey.cards.model.state;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.multiplayer.io.ITcpConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionAccepter;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceAnnouncer;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceRequester;
import gent.timdemey.cards.services.context.CommandHistory;

public class State extends EntityBase
{
    private CommandHistory commandHistory;

    public static final Property<CardGame> CardGame = Property.of(State.class, CardGame.class, "CardGame");
    public static final Property<UUID> LocalId = Property.of(State.class, UUID.class, "LocalId");
    public static final Property<String> LocalName = Property.of(State.class, String.class, "LocalName");
    public static final Property<Player> Players = Property.of(State.class, Player.class, "Players");
    public static final Property<UUID> ServerId = Property.of(State.class, UUID.class, "ServerId");
    public static final Property<String> ServerMsg = Property.of(State.class, String.class, "ServerMsg");
    public static final Property<Server> Servers = Property.of(State.class, Server.class, "Servers");
    public static final Property<TCP_ConnectionAccepter> TcpConnectionAccepter = Property.of(State.class, TCP_ConnectionAccepter.class,
        "TcpConnectionAccepter");
    public static final Property<TCP_ConnectionPool> TcpConnectionPool = Property.of(State.class, TCP_ConnectionPool.class, "TcpConnectionPool");
    public static final Property<ITcpConnectionListener> TcpConnectionListener = Property.of(State.class, ITcpConnectionListener.class,
        "TcpConnectionListener");
    public static final Property<UDP_ServiceAnnouncer> UdpServiceAnnouncer = Property.of(State.class, UDP_ServiceAnnouncer.class, "UdpServiceAnnouncer");
    public static final Property<UDP_ServiceRequester> UdpServiceRequester = Property.of(State.class, UDP_ServiceRequester.class, "UdpServiceRequester");

    // state lists
    private EntityStateListRef<Player> playersRef;
    private EntityStateListRef<Server> serversRef;

    // state values
    private StateValueRef<CardGame> cardGameRef;
    private StateValueRef<UUID> localIdRef;
    private StateValueRef<String> localNameRef;
    private StateValueRef<UUID> serverIdRef;
    private StateValueRef<String> serverMsgRef;

    // context specific
    // private StateValueRef<TCP_ConnectionCreator> tcpConnectionCreatorRef;
    private StateValueRef<TCP_ConnectionAccepter> tcpConnectionAccepterRef;
    private StateValueRef<TCP_ConnectionPool> tcpConnectionPoolRef;
    private StateValueRef<ITcpConnectionListener> tcpConnectionListenerRef;
    private StateValueRef<UDP_ServiceAnnouncer> udpServiceAnnouncerRef;
    private StateValueRef<UDP_ServiceRequester> udpServiceRequesterRef;

    public State()
    {
        cardGameRef = new StateValueRef<>(CardGame, id);
        localIdRef = new StateValueRef<>(LocalId, id);
        localNameRef = new StateValueRef<>(LocalName, id);
        playersRef = new EntityStateListRef<>(Players, id, new ArrayList<>());
        serverIdRef = new StateValueRef<>(ServerId, id);
        serverMsgRef = new StateValueRef<>(ServerMsg, id);
        serversRef = new EntityStateListRef<>(Servers, id, new ArrayList<>());

        // state.tcpConnectionCreatorRef = StateValueRef.create(state);
        tcpConnectionAccepterRef = new StateValueRef<>(TcpConnectionAccepter, id);
        tcpConnectionPoolRef = new StateValueRef<>(TcpConnectionPool, id);
        tcpConnectionListenerRef = new StateValueRef<>(TcpConnectionListener, id);
        udpServiceAnnouncerRef = new StateValueRef<>(UdpServiceAnnouncer, id);
        udpServiceRequesterRef = new StateValueRef<>(UdpServiceRequester, id);
    }

    public CardGame getCardGame()
    {
        return cardGameRef.get();
    }

    public void setCardGame(CardGame cardGame)
    {
        cardGameRef.set(cardGame);
    }

    /*
     * public TCP_ConnectionCreator getTcpConnectionCreator() { return
     * tcpConnectionCreatorRef.get(); }
     * 
     * public void setTcpConnectionCreator(TCP_ConnectionCreator
     * tcpConnectionCreator) { tcpConnectionCreatorRef.set(tcpConnectionCreator); }
     */

    public TCP_ConnectionAccepter getTcpConnectionAccepter()
    {
        return tcpConnectionAccepterRef.get();
    }

    public void setTcpConnectionAccepter(TCP_ConnectionAccepter tcpConnectionAccepter)
    {
        tcpConnectionAccepterRef.set(tcpConnectionAccepter);
    }

    public ITcpConnectionListener getTcpConnectionListener()
    {
        return tcpConnectionListenerRef.get();
    }

    public void setTcpConnectionListener(ITcpConnectionListener tcpConnectionListener)
    {
        tcpConnectionListenerRef.set(tcpConnectionListener);
    }

    public TCP_ConnectionPool getTcpConnectionPool()
    {
        return tcpConnectionPoolRef.get();
    }

    public void setTcpConnectionPool(TCP_ConnectionPool tcpConnectionPool)
    {
        tcpConnectionPoolRef.set(tcpConnectionPool);
    }

    public UDP_ServiceRequester getUdpServiceRequester()
    {
        return udpServiceRequesterRef.get();
    }

    public void setUdpServiceRequester(UDP_ServiceRequester udpServiceRequester)
    {
        udpServiceRequesterRef.set(udpServiceRequester);
    }

    public UDP_ServiceAnnouncer getUdpServiceAnnouncer()
    {
        return udpServiceAnnouncerRef.get();
    }

    public void setUdpServiceAnnouncer(UDP_ServiceAnnouncer udpServiceAnnouncer)
    {
        udpServiceAnnouncerRef.set(udpServiceAnnouncer);
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
        return playersRef.getExcept(serverIdRef, localIdRef);
    }

    public UUID getServerId()
    {
        return serverIdRef.get();
    }

    public void setServerId(UUID serverId)
    {
        serverIdRef.set(serverId);
    }

    public EntityStateListRef<Server> getServers()
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
        this.commandHistory = commandHistory;
    }

    public CommandHistory getCommandHistory()
    {
        return commandHistory;
    }
    
    @Override
    public final String toDebugString()
    {
        return "State"; 
    }
}
