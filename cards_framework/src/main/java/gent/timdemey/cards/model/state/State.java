package gent.timdemey.cards.model.state;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.model.Player;
import gent.timdemey.cards.model.cards.CardGame;
import gent.timdemey.cards.model.commands.CommandHistory;
import gent.timdemey.cards.model.multiplayer.Server;
import gent.timdemey.cards.multiplayer.io.ITcpConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionAccepter;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceAnnouncer;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceRequester;

public class State extends EntityBase
{
    public static final Property CardGame = Property.of(State.class, "CardGame");
    public static final Property LocalId = Property.of(State.class, "LocalId");
    public static final Property LocalName = Property.of(State.class, "LocalName");
    public static final Property Players = Property.of(State.class, "Players");
    public static final Property ServerId = Property.of(State.class, "ServerId");
    public static final Property ServerMsg = Property.of(State.class, "ServerMsg");
    public static final Property Servers = Property.of(State.class, "Servers");
    public static final Property TcpConnectionAccepter = Property.of(State.class, "TcpConnectionAccepter");
    public static final Property TcpConnectionPool = Property.of(State.class, "TcpConnectionPool");
    public static final Property TcpConnectionListener = Property.of(State.class, "TcpConnectionListener");
    public static final Property UdpServiceAnnouncer = Property.of(State.class, "UdpServiceAnnouncer");
    public static final Property UdpServiceRequester = Property.of(State.class, "UdpServiceRequester");

    private final CommandHistory commandHistory;

    // state lists
    private EntityStateListRef<Player> playersRef;
    private StateListRef<Server> serversRef;

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
        super(UUID.randomUUID());

        cardGameRef = new StateValueRef<>(CardGame, id);
        localIdRef = new StateValueRef<>(LocalId, id);
        localNameRef = new StateValueRef<>(LocalName, id);
        playersRef = new EntityStateListRef<>(Players, id, new ArrayList<>());
        serverIdRef = new StateValueRef<>(ServerId, id);
        serverMsgRef = new StateValueRef<>(ServerMsg, id);
        serversRef = new StateListRef<>(Servers, id, new ArrayList<>());

        // state.tcpConnectionCreatorRef = StateValueRef.create(state);
        tcpConnectionAccepterRef = new StateValueRef<>(TcpConnectionAccepter, id);
        tcpConnectionPoolRef = new StateValueRef<>(TcpConnectionPool, id);
        tcpConnectionListenerRef = new StateValueRef<>(TcpConnectionListener, id);
        udpServiceAnnouncerRef = new StateValueRef<>(UdpServiceAnnouncer, id);
        udpServiceRequesterRef = new StateValueRef<>(UdpServiceRequester, id);

        this.commandHistory = new CommandHistory();
    }

    public CardGame getCardGame()
    {
        return cardGameRef.get();
    }

    public void setCardGame(CardGame cardGame)
    {
        cardGameRef.set(cardGame);
    }

    public CommandHistory getCommandHistory()
    {
        return commandHistory;
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

    public StateListRef<Server> getServers()
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
}
