package gent.timdemey.cards.model.entities.commands.dialogs;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.CommandPayloadBase;
import gent.timdemey.cards.services.context.ContextType;

public abstract class DialogCommandBase<CMDPAYLOAD extends CommandPayloadBase> extends CommandBase<CMDPAYLOAD>
{
    protected DialogCommandBase(Container container, CMDPAYLOAD payload)
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
