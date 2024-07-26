package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import java.util.UUID;

/**
 * Special meta command to redo the last undone command. This command should not
 * be tracked and can as such not be undone or re-executed.
 * 
 * @author Timmos
 */
public final class C_Redo extends CommandBase
{
    C_Redo(
        IContextService contextService, 
        UUID id)
    {
        super(contextService, id);
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        if (state.getCommandHistory() == null)
        {
            return CanExecuteResponse.no("State.CommandHistory is null");
        }
        if (!state.getCommandHistory().canRedo())
        {
            return CanExecuteResponse.no("CommandHistory cannot redo");
        }
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        state.getCommandHistory().redo(state);
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
