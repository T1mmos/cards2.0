package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public abstract class DialogCommandBase extends CommandBase
{
    protected IDialogService dialogServ;
    protected DialogCommandBase()
    {
        super();
    }

    @Override
    protected final boolean canExecute(Context context, ContextType type, State state)
    {
        CheckNotContext(ContextType.Client, ContextType.Server);
        
        return canShowDialog(context, type, state);
    }

    protected abstract boolean canShowDialog(Context context, ContextType type, State state);

    @Override
    protected final void execute(Context context, ContextType type, State state)
    {
        CheckNotContext(ContextType.Client, ContextType.Server);
        
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
