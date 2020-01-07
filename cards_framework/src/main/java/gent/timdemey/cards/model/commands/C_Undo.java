package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Special meta command to undo the last command. This command should not be
 * tracked and can as such not be undone or re-executed.
 * 
 * @author Timmos
 */
public final class C_Undo extends CommandBase
{
	C_Undo()
	{
	}

	@Override
	protected boolean canExecute(Context context, ContextType type, State state)
	{
		return true;
	}

	@Override
	protected void execute(Context context, ContextType type, State state)
	{
		CommandHistory history = state.getCommandHistory();

		if (!history.canUndo())
		{
			throw new IllegalStateException("Not in the redoable state");
		}

		CommandBase cmdToUndo = history.execLine.get(history.current).getCommand();
		cmdToUndo.undo(context, type, state);
		history.current--;
	}
}
