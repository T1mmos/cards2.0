package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Undo;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;

/**
 * Special meta command to undo the last command. This command should not be
 * tracked and can as such not be undone or re-executed.
 * 
 * @author Timmos
 */
public final class C_Undo extends CommandBase
{
    public C_Undo(
        IContextService contextService, State state,
        P_Undo parameters)
    {
        super(contextService, state, parameters);
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        if (_State.getCommandHistory() == null)
        {
            return CanExecuteResponse.no("State.CommandHistory is null");
        }
        if (!_State.getCommandHistory().canUndo())
        {
            return CanExecuteResponse.no("CommandHistory cannot undo");
        }

        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type)
    {
        _State.getCommandHistory().undo();
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
