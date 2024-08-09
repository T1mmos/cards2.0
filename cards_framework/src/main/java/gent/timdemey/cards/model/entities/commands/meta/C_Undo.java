package gent.timdemey.cards.model.entities.commands.meta;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;

/**
 * Special meta command to undo the last command. This command should not be
 * tracked and can as such not be undone or re-executed.
 * 
 * @author Timmos
 */
public final class C_Undo extends CommandBase<P_Undo>
{
    public C_Undo(
        Container container,
        P_Undo parameters)
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
        if (!_State.getCommandHistory().canUndo())
        {
            return CanExecuteResponse.no("CommandHistory cannot undo");
        }

        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        _State.getCommandHistory().undo();
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
