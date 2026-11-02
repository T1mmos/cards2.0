package gent.timdemey.cards.protocol;

import java.util.UUID;

import gent.timdemey.cards.serialization.dto.cards.CardGameDto;
import gent.timdemey.cards.serialization.dto.commands.CommandBaseDto;

/**
 * All messages the server can send to a client. Only clients receive these.
 */
public sealed interface ServerMsg
{
	/** A reply to a specific {@link ClientMsg} request, matched by reqId. */
	sealed interface Response extends ServerMsg
	{
		long reqId();
	}

	/** Pushed by the server, not a reply to a request. */
	sealed interface Event extends ServerMsg
	{
	}

	// --- responses ---

	record HelloOk(long reqId, UUID serverId, String serverName) implements Response {}

	/** Entering the lobby; carries the full lobby state the client starts from. */
	record JoinLobbyOk(long reqId, LobbyState lobby) implements Response {}

	record Ok(long reqId) implements Response {}

	record Error(long reqId, String reason) implements Response {}

	// --- lobby events (numbered deltas, see Versioned) ---

	record PlayerJoined(long version, LobbyState.PlayerInfo player) implements Event, Versioned {}

	record PlayerLeft(long version, UUID playerId) implements Event, Versioned {}

	record LobbyAdminChanged(long version, UUID lobbyAdminId) implements Event, Versioned {}

	// --- game events ---

	/** Full game state at a version. Sent at game start (version 0) or to a player entering an ongoing game. */
	record GameFull(long version, CardGameDto cardGame) implements Event, Versioned {}

	// numbered deltas, broadcast to all players so every client has the same log (see Versioned)

	/** A move of player playerId is accepted. The originating client recognizes its own move by cmdId. */
	record MoveAccepted(long version, UUID playerId, UUID cmdId, CommandBaseDto cmd) implements Event, Versioned {}

	/** A move of player playerId is refused (e.g. another player was first). The originating client removes it. */
	record MoveRejected(long version, UUID playerId, UUID cmdId, String reason) implements Event, Versioned {}

	record GameEnded(long version, UUID winnerId) implements Event, Versioned {}

	/** The game is over for everyone (e.g. a player left) and all players return to the lobby. */
	record GameToLobby(String reason) implements Event {}

	/** The server is shutting down (e.g. the lobby admin left); the connection is closed afterwards. */
	record ServerStopping(String reason) implements Event {}
}
