package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.CommandPayloadBase;
import gent.timdemey.cards.services.context.ContextType;

public abstract class DialogCommandBase extends CommandBase
{
    protected DialogCommandBase(Container container, CommandPayloadBase payload)
    {
        super(container, payload);
    }

    @Override
    public final CanExecuteResponse canExecute()
    {
        CheckContext(ContextType.UI);

        return canShowDialog();
    }

    protected abstract CanExecuteResponse canShowDialog();

    @Override
    public final void execute()
    {
        CheckContext(ContextType.UI);
        showDialog();
    }

    protected abstract void showDialog();

    @Override
    public String toDebugString()
    {
        return "DialogCommandBase: " + getClass().getSimpleName();
    }

}
