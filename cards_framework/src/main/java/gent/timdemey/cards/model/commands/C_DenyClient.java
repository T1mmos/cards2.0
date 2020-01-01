package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.State;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_DenyClient extends CommandBase
{
    String serverMessage;
    
    C_DenyClient(String serverMessage) 
    {
        this.serverMessage = serverMessage;
    }

    @Override
    public void execute(Context context, ContextType type, State state) 
    {
        if (type == ContextType.Client)
        {
            reschedule(ContextType.UI);
        }
        else if (type == ContextType.UI)
        {
            Services.get(IDialogService.class).ShowMessage("test", "not welcome: "+serverMessage);
        }
        else 
        {
            throw new IllegalStateException();
        }
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return type == ContextType.Client || type == ContextType.UI;
    }
}
