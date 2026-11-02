package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.state.State;

public interface INetworkService
{
	/**
	 * Sends a synced move, already executed optimistically on the UI context, to the server for validation.
	 */
	void sendMove(State state, CommandBase command);
}
