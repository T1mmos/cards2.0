package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;

public abstract class DialogCommandBase extends CommandBase
{
    protected DialogCommandBase(IContextService contextService, State state, PayloadBase payload)
    {
        super(contextService, state, payload);
    }

    @Override
    protected final CanExecuteResponse canExecute(Context context, ContextType type)
    {
        CheckContext(type, ContextType.UI);

        return canShowDialog(context, type);
    }

    protected abstract CanExecuteResponse canShowDialog(Context context, ContextType type);

    @Override
    protected final void execute(Context context, ContextType type)
    {
        CheckContext(type, ContextType.UI);
        showDialog(context, type);
    }

    protected abstract void showDialog(Context context, ContextType type);

    @Override
    public String toDebugString()
    {
        return "DialogCommandBase: " + getClass().getSimpleName();
    }

}
