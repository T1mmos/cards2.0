package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.CommandHistory;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Special meta command to redo the last undone command. This command should not
 * be tracked and can as such not be undone or re-executed.
 * 
 * @author Timmos
 */
public final class C_Redo extends CommandBase
{
    public C_Redo()
    {
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return context.canRedo();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CommandHistory history = context.getCommandHistory().redo;

        // ICommandProcessor processor = context.getCommandProcessor();

        CommandBase cmdToRedo = history.undo execLine.get(history.current + 1).getCommand();

        cmdToRedo.execute(context, type, state);
        history.current++;
    }
}
