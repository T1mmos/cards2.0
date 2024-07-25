package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public abstract class DialogCommandBase extends CommandBase
{
   // protected IFrameSer dialogServ;

    protected DialogCommandBase()
    {
        super();
    }

    @Override
    protected final CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);

        return canShowDialog(context, type, state);
    }

    protected abstract CanExecuteResponse canShowDialog(Context context, ContextType type, State state);

    @Override
    protected final void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        showDialog(context, type, state);
    }

    protected abstract void showDialog(Context context, ContextType type, State state);

    @Override
    public String toDebugString()
    {
        return "DialogCommandBase: " + getClass().getSimpleName();
    }

}
