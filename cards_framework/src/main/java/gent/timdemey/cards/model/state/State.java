package gent.timdemey.cards.model.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.model.Player;
import gent.timdemey.cards.model.cards.CardGame;
import gent.timdemey.cards.model.commands.CommandHistory;
import gent.timdemey.cards.model.other.Server;
import gent.timdemey.cards.multiplayer.io.ITcpConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionAccepter;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceAnnouncer;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceRequester;
import gent.timdemey.cards.services.context.ContextType;

public class State extends EntityBase
{
	// calculated
	private final StateDelta stateDelta;
	private final CommandHistory history;

	// state lists
	private StateListRef<Player> playersRef;
	private StateListRef<Server> serversRef;

	// state values
	private StateValueRef<CardGame> cardGameRef;
	private StateValueRef<UUID> localIdRef;
	private StateValueRef<String> localNameRef;
	private StateValueRef<UUID> serverIdRef;
	private StateValueRef<String> serverMsgRef;

	// context specific
	//private StateValueRef<TCP_ConnectionCreator> tcpConnectionCreatorRef;
	private StateValueRef<TCP_ConnectionAccepter> tcpConnectionAccepterRef;
	private StateValueRef<TCP_ConnectionPool> tcpConnectionPoolRef;
	private StateValueRef<ITcpConnectionListener> tcpConnectionListenerRef;
	private StateValueRef<UDP_ServiceAnnouncer> udpServiceAnnouncerRef;
	private StateValueRef<UDP_ServiceRequester> udpServiceRequesterRef;

	private State()
	{
		super(null);

		this.stateDelta = new StateDelta();
		this.history = new CommandHistory();
	}

	public static State create(ContextType contextType)
	{
		State state = new State();

		state.cardGameRef = StateValueRef.create(state);
		state.localIdRef = StateValueRef.create(state);
		state.localNameRef = StateValueRef.create(state);
		state.playersRef = StateListRef.create(state, new ArrayList<>());
		state.serverIdRef = StateValueRef.create(state);
		state.serverMsgRef = StateValueRef.create(state);
		state.serversRef = StateListRef.create(state, new ArrayList<>());

		//state.tcpConnectionCreatorRef = StateValueRef.create(state);
		state.tcpConnectionAccepterRef = StateValueRef.create(state);
		state.tcpConnectionPoolRef = StateValueRef.create(state);
		state.tcpConnectionListenerRef = StateValueRef.create(state);
		state.udpServiceAnnouncerRef = StateValueRef.create(state);
		state.udpServiceRequesterRef = StateValueRef.create(state);

		return state;
	}

	public CardGame getCardGame()
	{
		return cardGameRef.get();
	}

	public void setCardGame(CardGame cardGame)
	{
		cardGameRef.set(cardGame);
	}

/*	public TCP_ConnectionCreator getTcpConnectionCreator()
	{
		return tcpConnectionCreatorRef.get();
	}

	public void setTcpConnectionCreator(TCP_ConnectionCreator tcpConnectionCreator)
	{
		tcpConnectionCreatorRef.set(tcpConnectionCreator);
	}*/
	
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

	public List<Player> getPlayers()
	{
		return Collections.unmodifiableList(playersRef);
	}

	public Player getPlayer(UUID id)
	{
		for (Player p : playersRef)
		{
			if (p.id.equals(id))
			{
				return p;
			}
		}
		return null;
	}
	


	public List<Player> getPlayersExcept(UUID clientId)
	{
		List<Player> players = getRemotePlayers();
		Player excluded = getPlayer(clientId);
		players.remove(excluded);
		return players;
	}

	public List<Player> getRemotePlayers()
	{
		List<Player> excluded = new ArrayList<>();

		if (serverIdRef.get() != null)
		{
			excluded.add(getPlayer(serverIdRef.get()));
		}
		if (localIdRef.get() != null)
		{
			excluded.add(getPlayer(localIdRef.get()));
		}
		List<Player> remotePlayers = new ArrayList<>(playersRef);
		remotePlayers.removeAll(excluded);
		return remotePlayers;
	}

	public void addPlayer(Player player)
	{
		playersRef.add(player);
	}

	public void removePlayer(Player player)
	{
		playersRef.remove(player);
	}

	public UUID getServerId()
	{
		return serverIdRef.get();
	}

	public void setServerId(UUID serverId)
	{
		serverIdRef.set(serverId);
	}

	public void addServer(Server server)
	{
		serversRef.add(server);
	}
	
	public void clearServers()
	{
		serversRef.clear();
	}
	
	public String getServerMessage()
	{
		return serverMsgRef.get();
	}

	public void setServerMessage(String serverMsg)
	{
		serverMsgRef.set(serverMsg);
	}

	StateDelta getStateDelta()
	{
		return stateDelta;
	}
}
