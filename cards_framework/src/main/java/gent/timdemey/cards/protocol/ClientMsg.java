package gent.timdemey.cards.protocol;

import java.util.UUID;

import gent.timdemey.cards.serialization.dto.commands.CommandBaseDto;

/**
 * All messages a client can send to the server. Only the server receives these.
 */
public sealed interface ClientMsg
{
	/** First message after the connection opens: identifies the client. */
	record Hello(long reqId, int protocolVersion, UUID clientId, String clientName) implements ClientMsg {}

	/** Asks to enter the lobby; answered by {@link ServerMsg.JoinLobbyOk} or {@link ServerMsg.Error}. */
	record JoinLobby(long reqId) implements ClientMsg {}

	/** Lobby admin asks to start the game; answered by {@link ServerMsg.Ok} or {@link ServerMsg.Error}. */
	record StartGame(long reqId) implements ClientMsg {}

	/** Client leaves the lobby or game gracefully. */
	record Leave() implements ClientMsg {}

	/** A move that the client already executed optimistically and awaits accept/reject for. */
	record MoveRequest(UUID cmdId, CommandBaseDto cmd) implements ClientMsg {}

	/**
	 * Client detected a version gap; the server resends the numbered events fromVersion..toVersion (inclusive)
	 * of the given scope, exactly as they were sent originally.
	 */
	record RequestMissedEvents(Scope scope, long fromVersion, long toVersion) implements ClientMsg {}

	enum Scope
	{
		LOBBY, GAME
	}
}
