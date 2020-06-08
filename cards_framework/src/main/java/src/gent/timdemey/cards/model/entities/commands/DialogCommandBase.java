package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IDialogService;

public abstract class DialogCommandBase extends CommandBase
{
    protected IDialogService dialogServ;

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
    protected final void preExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);

        dialogServ = Services.get(IDialogService.class);
        showDialog(context, type, state);
    }

    protected abstract void showDialog(Context context, ContextType type, State state);

    @Override
    public String toDebugString()
    {
        return "DialogCommandBase: " + getClass().getSimpleName();
    }

}
