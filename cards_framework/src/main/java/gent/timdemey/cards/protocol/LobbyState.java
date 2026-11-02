package gent.timdemey.cards.protocol;

import java.util.List;
import java.util.UUID;

/**
 * Full lobby state at a given version: the accumulation of all lobby deltas up to that version.
 * Only the lobby part of the server state; game state is versioned separately.
 */
public record LobbyState(long version, String serverMessage, UUID lobbyAdminId, List<PlayerInfo> players) implements Versioned
{
	public record PlayerInfo(UUID id, String name)
	{
	}
}
