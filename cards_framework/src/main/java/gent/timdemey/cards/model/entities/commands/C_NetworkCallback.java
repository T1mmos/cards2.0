package gent.timdemey.cards.model.entities.commands;

import java.util.function.Consumer;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

/**
 * Local-only command that runs network handling logic on the context thread owning the State.
 * Network callbacks arrive on I/O threads and must never touch State directly.
 */
public final class C_NetworkCallback extends CommandBase
{
	private final String description;
	private final Consumer<State> action;

	public C_NetworkCallback(String description, Consumer<State> action)
	{
		this.description = description;
		this.action = action;
	}

	@Override
	protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
	{
		return CanExecuteResponse.yes();
	}

	@Override
	protected void execute(Context context, ContextType type, State state)
	{
		action.accept(state);
	}

	@Override
	public String toDebugString()
	{
		return Debug.getKeyValue("description", description);
	}
}
