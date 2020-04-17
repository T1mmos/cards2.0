package gent.timdemey.cards.model.entities.commands;

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
    public C_Undo()
    {
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        if (state.getCommandHistory() == null)
        {
            return false;
        }
        return state.getCommandHistory().canUndo();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        state.getCommandHistory().undo(state);
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
