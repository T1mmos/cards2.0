package gent.timdemey.cards.netcode.ws;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.commands.C_Accept;
import gent.timdemey.cards.model.entities.commands.C_Disconnect;
import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.C_NetworkCallback;
import gent.timdemey.cards.model.entities.commands.C_OnGameEnded;
import gent.timdemey.cards.model.entities.commands.C_OnGameToLobby;
import gent.timdemey.cards.model.entities.commands.C_OnGameToLobby.GameToLobbyReason;
import gent.timdemey.cards.model.entities.commands.C_OnLobbyToGame;
import gent.timdemey.cards.model.entities.commands.C_Reject;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.D_Connect;
import gent.timdemey.cards.model.entities.commands.D_ShowLobby;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.entities.game.payload.P_Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.protocol.ClientMsg;
import gent.timdemey.cards.protocol.LobbyState;
import gent.timdemey.cards.protocol.ServerMsg;
import gent.timdemey.cards.protocol.Versioned;
import gent.timdemey.cards.protocol.ws.IWsClientListener;
import gent.timdemey.cards.protocol.ws.WsClient;
import gent.timdemey.cards.protocol.ws.WsRequestException;
import gent.timdemey.cards.serialization.dto.commands.CommandBaseDto;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.ISerializationService;

/**
 * Client side of the multiplayer protocol: one WebSocket connection to the server that carries all
 * communication. Performs the handshake (Hello, JoinLobby) as non-blocking request/responses and
 * applies the versioned lobby events in order, requesting missed ones on a gap.
 * <p>
 * All State changes run on the UI context thread.
 */
public final class GameClient
{
	private final WsClient wsClient;
	private volatile boolean closedLocally = false;

	// only accessed on the UI thread
	private long lobbyVersion = -1;
	private final TreeMap<Long, ServerMsg.Event> pendingLobbyEvents = new TreeMap<>();
	private long gameVersion = -1;
	private final TreeMap<Long, ServerMsg.Event> pendingGameEvents = new TreeMap<>();

	public GameClient(String host, int port)
	{
		this.wsClient = new WsClient(host, port, new Listener());
	}

	/**
	 * Connects, identifies and joins the lobby. Never blocks; on success the lobby is shown, on failure
	 * the state is cleaned up and the server browser is shown again.
	 */
	public void connectAndJoin(UUID clientId, String clientName)
	{
		wsClient.connect()
			.thenCompose(v -> wsClient.request(reqId -> new ClientMsg.Hello(reqId, GameServer.PROTOCOL_VERSION, clientId, clientName), ServerMsg.HelloOk.class))
			.thenCompose(ok -> wsClient.request(ClientMsg.JoinLobby::new, ServerMsg.JoinLobbyOk.class))
			.whenComplete((ok, ex) -> scheduleOnUi("join lobby result", state -> onJoinResult(state, ok, ex)));
	}

	public CompletableFuture<ServerMsg.Ok> requestStartGame()
	{
		return wsClient.request(ClientMsg.StartGame::new, ServerMsg.Ok.class);
	}

	public void sendMove(UUID cmdId, CommandBaseDto cmd)
	{
		wsClient.send(new ClientMsg.MoveRequest(cmdId, cmd));
	}

	public void close()
	{
		closedLocally = true;
		wsClient.close();
	}

	private boolean isCurrent(State state)
	{
		return state.getGameClient() == this;
	}

	private void onJoinResult(State state, ServerMsg.JoinLobbyOk ok, Throwable ex)
	{
		if (!isCurrent(state))
		{
			return;
		}

		if (ex != null)
		{
			Throwable cause = ex instanceof CompletionException && ex.getCause() != null ? ex.getCause() : ex;
			Logger.warn("Joining the lobby failed: %s", cause.getMessage());

			state.setGameClient(null);
			state.setServer(null);
			state.setGameState(GameState.Disconnected);

			boolean lobbyFull = cause instanceof WsRequestException && GameServer.ERR_LOBBY_FULL.equals(cause.getMessage());
			String title = Loc.get(lobbyFull ? LocKey.DialogTitle_lobbyFull : LocKey.DialogTitle_connectionlost);
			String msg = Loc.get(lobbyFull ? LocKey.DialogMessage_lobbyFull : LocKey.DialogMessage_connectionlost);
			Services.get(IFrameService.class).showMessage(title, msg, c -> scheduleOnUi(new D_Connect()));
			return;
		}

		LobbyState lobby = ok.lobby();
		List<Player> players = new ArrayList<>();
		for (LobbyState.PlayerInfo info : lobby.players())
		{
			if (!state.getPlayers().contains(info.id()))
			{
				players.add(toPlayer(info));
			}
		}

		state.setServerMessage(lobby.serverMessage());
		state.setLobbyAdminId(lobby.lobbyAdminId());
		state.getPlayers().addAll(players);
		state.setGameState(GameState.Lobby);
		lobbyVersion = lobby.version();

		scheduleOnUi(new D_ShowLobby());
		applyPendingLobbyEvents(state);
	}

	private void onEvent(State state, ServerMsg.Event event)
	{
		if (!isCurrent(state))
		{
			return;
		}

		switch (event)
		{
			case ServerMsg.PlayerJoined e -> onLobbyEvent(state, e);
			case ServerMsg.PlayerLeft e -> onLobbyEvent(state, e);
			case ServerMsg.LobbyAdminChanged e -> onLobbyEvent(state, e);
			case ServerMsg.GameFull e -> onGameFull(state, e);
			case ServerMsg.MoveAccepted e -> onGameEvent(state, e);
			case ServerMsg.MoveRejected e -> onGameEvent(state, e);
			case ServerMsg.GameEnded e -> onGameEvent(state, e);
			case ServerMsg.GameToLobby e -> onGameToLobby(state, e);
			case ServerMsg.ServerStopping e -> onServerStopping(state, e);
		}
	}

	private void onLobbyEvent(State state, ServerMsg.Event event)
	{
		long version = ((Versioned) event).version();
		if (lobbyVersion >= 0 && version <= lobbyVersion)
		{
			return;
		}
		pendingLobbyEvents.put(version, event);
		applyPendingLobbyEvents(state);
	}

	private void applyPendingLobbyEvents(State state)
	{
		if (lobbyVersion < 0)
		{
			return; // not in the lobby yet, keep buffering
		}

		pendingLobbyEvents.headMap(lobbyVersion, true).clear();
		while (!pendingLobbyEvents.isEmpty() && pendingLobbyEvents.firstKey() == lobbyVersion + 1)
		{
			ServerMsg.Event event = pendingLobbyEvents.pollFirstEntry().getValue();
			applyLobbyEvent(state, event);
			lobbyVersion++;
		}

		if (!pendingLobbyEvents.isEmpty())
		{
			long from = lobbyVersion + 1;
			long to = pendingLobbyEvents.firstKey() - 1;
			Logger.info("Lobby version gap detected, requesting events %s..%s", from, to);
			wsClient.send(new ClientMsg.RequestMissedEvents(ClientMsg.Scope.LOBBY, from, to));
		}
	}

	private void applyLobbyEvent(State state, ServerMsg.Event event)
	{
		switch (event)
		{
			case ServerMsg.PlayerJoined e ->
			{
				if (!state.getPlayers().contains(e.player().id()))
				{
					state.getPlayers().add(toPlayer(e.player()));
				}
			}
			case ServerMsg.PlayerLeft e ->
			{
				if (state.getPlayers().contains(e.playerId()))
				{
					state.getPlayers().remove(e.playerId());
				}
			}
			case ServerMsg.LobbyAdminChanged e -> state.setLobbyAdminId(e.lobbyAdminId());
			default -> throw new IllegalArgumentException("Not a lobby event: " + event);
		}
	}

	private void onGameEvent(State state, ServerMsg.Event event)
	{
		long version = ((Versioned) event).version();
		if (gameVersion >= 0 && version <= gameVersion)
		{
			return;
		}
		pendingGameEvents.put(version, event);
		applyPendingGameEvents(state);
	}

	private void applyPendingGameEvents(State state)
	{
		if (gameVersion < 0)
		{
			return; // game not started yet, keep buffering
		}

		pendingGameEvents.headMap(gameVersion, true).clear();
		while (!pendingGameEvents.isEmpty() && pendingGameEvents.firstKey() == gameVersion + 1)
		{
			ServerMsg.Event event = pendingGameEvents.pollFirstEntry().getValue();
			applyGameEvent(state, event);
			gameVersion++;
		}

		if (!pendingGameEvents.isEmpty())
		{
			long from = gameVersion + 1;
			long to = pendingGameEvents.firstKey() - 1;
			Logger.info("Game version gap detected, requesting events %s..%s", from, to);
			wsClient.send(new ClientMsg.RequestMissedEvents(ClientMsg.Scope.GAME, from, to));
		}
	}

	// runs on the UI thread; the resulting commands are scheduled so they run in event order
	private void applyGameEvent(State state, ServerMsg.Event event)
	{
		UUID localId = state.getLocalId();
		UUID serverId = state.getServerId();

		switch (event)
		{
			case ServerMsg.MoveAccepted e ->
			{
				CommandBase command;
				if (localId.equals(e.playerId()))
				{
					command = new C_Accept(e.cmdId());
				}
				else
				{
					command = Services.get(ISerializationService.class).getCommandDtoMapper().fromCommandDto(e.cmd());
				}
				command.setSourceId(serverId);
				scheduleOnUi(command);
			}
			case ServerMsg.MoveRejected e ->
			{
				if (localId.equals(e.playerId()))
				{
					Logger.info("Own move %s was rejected: %s", e.cmdId(), e.reason());
					C_Reject command = new C_Reject(e.cmdId());
					command.setSourceId(serverId);
					scheduleOnUi(command);
				}
				else
				{
					Logger.info("Move %s of player %s was rejected: %s", e.cmdId(), e.playerId(), e.reason());
				}
			}
			case ServerMsg.GameEnded e ->
			{
				C_OnGameEnded command = new C_OnGameEnded(e.winnerId());
				command.setSourceId(serverId);
				scheduleOnUi(command);
			}
			default -> throw new IllegalArgumentException("Not a game event: " + event);
		}
	}

	private void onGameFull(State state, ServerMsg.GameFull e)
	{
		CardGame cardGame = Services.get(ISerializationService.class).getCommandDtoMapper().fromCardGameDto(e.cardGame());
		C_OnLobbyToGame command = new C_OnLobbyToGame(cardGame);
		command.setSourceId(state.getServerId());
		Logger.info("Game started at game version %s", e.version());
		scheduleOnUi(command);

		// a new game: events of an earlier game are obsolete, events buffered for this game are applied after the start
		pendingGameEvents.headMap(e.version(), true).clear();
		gameVersion = e.version();
		applyPendingGameEvents(state);
	}

	private void onGameToLobby(State state, ServerMsg.GameToLobby e)
	{
		gameVersion = -1;
		pendingGameEvents.clear();

		GameToLobbyReason reason;
		try
		{
			reason = GameToLobbyReason.valueOf(e.reason());
		}
		catch (IllegalArgumentException | NullPointerException ex)
		{
			Logger.warn("Unknown game to lobby reason '%s'", e.reason());
			reason = GameToLobbyReason.PlayerLeft;
		}
		C_OnGameToLobby command = new C_OnGameToLobby(reason);
		command.setSourceId(state.getServerId());
		scheduleOnUi(command);
	}

	private void onServerStopping(State state, ServerMsg.ServerStopping e)
	{
		Logger.info("Server is stopping: %s", e.reason());
		closedLocally = true;
		if (state.getGameState() != GameState.Disconnected)
		{
			C_Disconnect command = new C_Disconnect(DisconnectReason.LobbyAdminLeft);
			command.setSourceId(state.getServerId());
			scheduleOnUi(command);
		}
		wsClient.close();
	}

	private void onClosed(State state)
	{
		if (!isCurrent(state) || closedLocally)
		{
			return;
		}
		if (state.getGameState() != GameState.Disconnected)
		{
			scheduleOnUi(new C_Disconnect(DisconnectReason.ConnectionLost));
		}
	}

	private static Player toPlayer(LobbyState.PlayerInfo info)
	{
		P_Player pl = new P_Player();
		{
			pl.id = info.id();
			pl.name = info.name();
		}
		return new Player(pl);
	}

	private static void scheduleOnUi(CommandBase command)
	{
		Services.get(IContextService.class).getContext(ContextType.UI).schedule(command);
	}

	private static void scheduleOnUi(String description, Consumer<State> action)
	{
		scheduleOnUi(new C_NetworkCallback(description, action));
	}

	private final class Listener implements IWsClientListener
	{
		@Override
		public void onEvent(ServerMsg.Event event)
		{
			scheduleOnUi("event " + event.getClass().getSimpleName(), state -> GameClient.this.onEvent(state, event));
		}

		@Override
		public void onClose(String reason, boolean byRemote)
		{
			scheduleOnUi("connection closed", GameClient.this::onClosed);
		}
	}
}
