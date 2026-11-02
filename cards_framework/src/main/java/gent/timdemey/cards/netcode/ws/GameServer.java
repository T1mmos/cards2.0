package gent.timdemey.cards.netcode.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.commands.C_NetworkCallback;
import gent.timdemey.cards.model.entities.commands.C_RemovePlayer;
import gent.timdemey.cards.model.entities.commands.C_StartMultiplayerGame;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.payload.P_Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.protocol.ClientMsg;
import gent.timdemey.cards.protocol.LobbyState;
import gent.timdemey.cards.protocol.ServerMsg;
import gent.timdemey.cards.protocol.ws.IWsServerListener;
import gent.timdemey.cards.protocol.ws.WsServer;
import gent.timdemey.cards.protocol.ws.WsSession;
import gent.timdemey.cards.serialization.dto.cards.CardGameDto;
import gent.timdemey.cards.serialization.dto.commands.CommandBaseDto;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.ISerializationService;

/**
 * Server side of the multiplayer protocol. Owns the WebSocket server and keeps, per connection, the
 * handshake state: a session is identified after Hello, and is in the lobby after JoinLobby.
 * <p>
 * All handling runs on the Server context thread: WebSocket callbacks only schedule work.
 * The lobby is versioned: every lobby change is a numbered event that is kept in a log so
 * clients can request missed events.
 */
public final class GameServer
{
	public static final int PROTOCOL_VERSION = 1;
	public static final String ERR_LOBBY_FULL = "LobbyFull";

	private final WsServer wsServer;
	private volatile boolean stopped = false;

	// only accessed on the Server context thread
	private final Map<UUID, WsSession> identified = new ConcurrentHashMap<>();
	private final Map<UUID, String> names = new ConcurrentHashMap<>();
	private final List<UUID> inLobby = new ArrayList<>();
	private final List<ServerMsg.Event> lobbyLog = new ArrayList<>();
	private long lobbyVersion = 0;
	private final List<ServerMsg.Event> gameLog = new ArrayList<>();
	private long gameVersion = 0;
	private UUID pendingWinnerId = null;

	public GameServer(int port)
	{
		this.wsServer = new WsServer(port, new Listener());
	}

	public CompletableFuture<Void> start()
	{
		return wsServer.start();
	}

	public void stop()
	{
		stopped = true;
		wsServer.stop();
	}

	/**
	 * A new game starts: resets the game event log and sends the full game state (version 0) to all players.
	 * Must be called on the Server context thread.
	 */
	public void onGameStarted(State state, CardGame cardGame)
	{
		gameLog.clear();
		gameVersion = 0;
		pendingWinnerId = null;
		CardGameDto dto = Services.get(ISerializationService.class).getCommandDtoMapper().toCardGameDto(cardGame);
		sendToPlayers(state, new ServerMsg.GameFull(gameVersion, dto));
	}

	/**
	 * The game has a winner. Called while the winning move executes, so the GameEnded event is broadcast
	 * right after that move's MoveAccepted event, keeping the event order equal to the execution order.
	 */
	public void onGameEnded(UUID winnerId)
	{
		pendingWinnerId = winnerId;
	}

	/**
	 * The game is aborted and all remaining players return to the lobby.
	 */
	public void onGameToLobby(State state, String reason)
	{
		gameLog.clear();
		sendToPlayers(state, new ServerMsg.GameToLobby(reason));
	}

	/**
	 * The server is about to stop: inform all identified clients so they can disconnect gracefully.
	 */
	public void onServerStopping(String reason)
	{
		ServerMsg.ServerStopping msg = new ServerMsg.ServerStopping(reason);
		for (WsSession session : identified.values())
		{
			session.send(msg);
		}
	}

	/**
	 * Must be called on the Server context thread after the player was removed from the state.
	 */
	public void onPlayerLeft(UUID playerId)
	{
		inLobby.remove(playerId);
		broadcastLobbyEvent(new ServerMsg.PlayerLeft(++lobbyVersion, playerId), null);
	}

	/**
	 * A synced move was accepted and executed by the server: broadcast it to all players as a numbered event.
	 */
	public void onMoveAccepted(State state, CommandBase command)
	{
		CommandBaseDto dto = Services.get(ISerializationService.class).getCommandDtoMapper().toCommandDto(command);
		broadcastGameEvent(state, new ServerMsg.MoveAccepted(++gameVersion, command.getSourceId(), command.id, dto));

		if (pendingWinnerId != null)
		{
			UUID winnerId = pendingWinnerId;
			pendingWinnerId = null;
			broadcastGameEvent(state, new ServerMsg.GameEnded(++gameVersion, winnerId));
		}
	}

	/**
	 * A synced move was refused by the server: broadcast the rejection to all players as a numbered event.
	 */
	public void onMoveRejected(State state, CommandBase command, String reason)
	{
		broadcastGameEvent(state, new ServerMsg.MoveRejected(++gameVersion, command.getSourceId(), command.id, reason));
	}

	private void broadcastGameEvent(State state, ServerMsg.Event event)
	{
		gameLog.add(event);
		sendToPlayers(state, event);
	}

	private void sendToPlayers(State state, ServerMsg msg)
	{
		for (UUID clientId : state.getPlayers().getIds())
		{
			WsSession session = identified.get(clientId);
			if (session != null)
			{
				session.send(msg);
			}
		}
	}

	private void broadcastLobbyEvent(ServerMsg.Event event, UUID excluded)
	{
		lobbyLog.add(event);
		for (UUID clientId : inLobby)
		{
			if (clientId.equals(excluded))
			{
				continue;
			}
			WsSession session = identified.get(clientId);
			if (session != null)
			{
				session.send(event);
			}
		}
	}

	private void scheduleOnServer(String description, Consumer<State> action)
	{
		if (stopped)
		{
			return;
		}
		try
		{
			IContextService ctxtServ = Services.get(IContextService.class);
			if (ctxtServ.isInitialized(ContextType.Server))
			{
				ctxtServ.getContext(ContextType.Server).schedule(new C_NetworkCallback(description, action));
			}
		}
		catch (Exception ex)
		{
			Logger.error("Cannot schedule network callback on the server context", ex);
		}
	}

	private void handle(State state, WsSession session, ClientMsg msg)
	{
		UUID clientId = session.getClientId();
		if (clientId == null && !(msg instanceof ClientMsg.Hello))
		{
			Logger.warn("Ignoring %s from unidentified session %s", msg.getClass().getSimpleName(), session);
			return;
		}

		switch (msg)
		{
			case ClientMsg.Hello hello -> onHello(state, session, hello);
			case ClientMsg.JoinLobby join -> onJoinLobby(state, session, join);
			case ClientMsg.StartGame start -> onStartGame(state, session, start);
			case ClientMsg.Leave leave -> session.close();
			case ClientMsg.RequestMissedEvents req -> onRequestMissedEvents(session, req);
			case ClientMsg.MoveRequest move -> onMoveRequest(session, move);
		}
	}

	private void onHello(State state, WsSession session, ClientMsg.Hello hello)
	{
		if (hello.protocolVersion() != PROTOCOL_VERSION)
		{
			session.send(new ServerMsg.Error(hello.reqId(), "Unsupported protocol version " + hello.protocolVersion()));
			return;
		}
		if (session.getClientId() != null || identified.containsKey(hello.clientId()))
		{
			session.send(new ServerMsg.Error(hello.reqId(), "Client already identified"));
			return;
		}

		boolean isAdmin = hello.clientId().equals(state.getLobbyAdminId());
		if (!isAdmin)
		{
			int max = Services.get(ICardPlugin.class).getPlayerCount();
			long pending = identified.keySet().stream().filter(id -> !state.getPlayers().contains(id)).count();
			if (state.getPlayers().size() + pending >= max)
			{
				session.send(new ServerMsg.Error(hello.reqId(), ERR_LOBBY_FULL));
				return;
			}
		}

		session.setClientId(hello.clientId());
		identified.put(hello.clientId(), session);
		names.put(hello.clientId(), hello.clientName());
		Logger.info("Client %s (id %s) identified from %s", hello.clientName(), hello.clientId(), session.getRemote());

		session.send(new ServerMsg.HelloOk(hello.reqId(), state.getServerId(), state.getServer().serverName));
	}

	private void onJoinLobby(State state, WsSession session, ClientMsg.JoinLobby join)
	{
		UUID clientId = session.getClientId();
		if (inLobby.contains(clientId))
		{
			session.send(new ServerMsg.Error(join.reqId(), "Already in the lobby"));
			return;
		}

		Player player = state.getPlayers().contains(clientId) ? state.getPlayers().get(clientId) : null;
		boolean added = false;
		if (player == null)
		{
			P_Player pl = new P_Player();
			{
				pl.id = clientId;
				pl.name = names.get(clientId);
			}
			player = new Player(pl);
			state.getPlayers().add(player);
			added = true;
		}
		inLobby.add(clientId);

		if (added)
		{
			broadcastLobbyEvent(new ServerMsg.PlayerJoined(++lobbyVersion, toInfo(player)), clientId);
		}

		List<LobbyState.PlayerInfo> players = new ArrayList<>();
		for (Player p : state.getPlayers())
		{
			players.add(toInfo(p));
		}
		LobbyState lobby = new LobbyState(lobbyVersion, state.getServerMessage(), state.getLobbyAdminId(), players);
		session.send(new ServerMsg.JoinLobbyOk(join.reqId(), lobby));
	}

	private void onStartGame(State state, WsSession session, ClientMsg.StartGame start)
	{
		C_StartMultiplayerGame cmd = new C_StartMultiplayerGame();
		cmd.setSourceId(session.getClientId());

		CanExecuteResponse resp = cmd.canExecute(state);
		if (!resp.canExecute())
		{
			session.send(new ServerMsg.Error(start.reqId(), resp.reason));
			return;
		}

		Services.get(IContextService.class).getContext(ContextType.Server).schedule(cmd);
		session.send(new ServerMsg.Ok(start.reqId()));
	}

	private void onRequestMissedEvents(WsSession session, ClientMsg.RequestMissedEvents req)
	{
		List<ServerMsg.Event> log = req.scope() == ClientMsg.Scope.GAME ? gameLog : lobbyLog;
		for (ServerMsg.Event event : log)
		{
			long version = ((gent.timdemey.cards.protocol.Versioned) event).version();
			if (version >= req.fromVersion() && version <= req.toVersion())
			{
				session.send(event);
			}
		}
	}

	private void onMoveRequest(WsSession session, ClientMsg.MoveRequest move)
	{
		CommandBase command = Services.get(ISerializationService.class).getCommandDtoMapper().fromCommandDto(move.cmd());
		if (!command.id.equals(move.cmdId()))
		{
			Logger.warn("MoveRequest cmdId %s does not match command id %s, ignoring", move.cmdId(), command.id);
			return;
		}
		command.setSourceId(session.getClientId());
		Logger.info("Received move '%s' from '%s'", command.getName(), session.getRemote());

		Services.get(IContextService.class).getContext(ContextType.Server).schedule(command);
	}

	private void onClosed(State state, WsSession session)
	{
		UUID clientId = session.getClientId();
		if (clientId == null || identified.get(clientId) != session)
		{
			return;
		}
		identified.remove(clientId);
		names.remove(clientId);

		if (state.getPlayers().contains(clientId))
		{
			Services.get(IContextService.class).getContext(ContextType.Server).schedule(new C_RemovePlayer(clientId));
		}
		else
		{
			inLobby.remove(clientId);
		}
	}

	private static LobbyState.PlayerInfo toInfo(Player player)
	{
		return new LobbyState.PlayerInfo(player.id, player.getName());
	}

	private final class Listener implements IWsServerListener
	{
		@Override
		public void onOpen(WsSession session)
		{
			Logger.info("Client connected from %s", session.getRemote());
		}

		@Override
		public void onMessage(WsSession session, ClientMsg msg)
		{
			scheduleOnServer("handle " + msg.getClass().getSimpleName(), state -> handle(state, session, msg));
		}

		@Override
		public void onClose(WsSession session, String reason)
		{
			scheduleOnServer("session closed", state -> onClosed(state, session));
		}
	}
}
