package gent.timdemey.cards.services.context;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.netcode.ws.GameClient;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.interfaces.INetworkService;
import gent.timdemey.cards.services.interfaces.ISerializationService;

/**
 * Sends synced moves over the WebSocket connection as MoveRequest messages.
 */
public class CommandNetworkService implements INetworkService
{
	@Override
	public void sendMove(State state, CommandBase command)
	{
		if (state == null)
		{
			throw new NullPointerException("state");
		}
		if (command == null)
		{
			throw new NullPointerException("command");
		}

		UUID localId = state.getLocalId();
		if (command.getSourceId() == null)
		{
			command.setSourceId(localId);
		}

		// only send a command if the SourceId == LocalId, meaning the command
		// didn't originate from somewhere else.
		if (!command.getSourceId().equals(localId))
		{
			Logger.debug("The command %s is not a local command, not so sending it", command.getName());
			return;
		}

		GameClient gameClient = state.getGameClient();
		if (gameClient == null)
		{
			Logger.warn("No network connection, cannot send command %s", command.getName());
			return;
		}

		CommandDtoMapper dtoMapper = Services.get(ISerializationService.class).getCommandDtoMapper();
		gameClient.sendMove(command.id, dtoMapper.toCommandDto(command));
	}
}
