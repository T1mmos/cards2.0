package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.model.state.State;
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
        return state.getCommandHistory().canRedo();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        state.getCommandHistory().redo(state);
    }
}
