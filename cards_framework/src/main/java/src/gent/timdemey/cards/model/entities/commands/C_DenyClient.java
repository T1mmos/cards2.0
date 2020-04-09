package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.payload.P_DenyClient;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

public class C_DenyClient extends CommandBase
{
    public final String serverMessage;

    public C_DenyClient(String serverMessage)
    {
        this.serverMessage = serverMessage;
    }
    
    public C_DenyClient(P_DenyClient pl)
    {
        super(pl);
        this.serverMessage = pl.serverMessage;
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
            Services.get(IDialogService.class).ShowMessage("test", "not welcome: " + serverMessage);
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

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("serverMessage", serverMessage);
    }
}
