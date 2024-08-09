package gent.timdemey.cards.model.entities.commands.meta;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;

/**
 * Special meta command to redo the last undone command. This command should not
 * be tracked and can as such not be undone or re-executed.
 * 
 * @author Timmos
 */
public final class C_Redo extends CommandBase<P_Redo>
{
    public C_Redo(
        Container container,
        P_Redo parameters)
    {
        super(container, parameters);
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        if (_State.getCommandHistory() == null)
        {
            return CanExecuteResponse.no("State.CommandHistory is null");
        }
        if (!_State.getCommandHistory().canRedo())
        {
            return CanExecuteResponse.no("CommandHistory cannot redo");
        }
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        _State.getCommandHistory().redo();
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
