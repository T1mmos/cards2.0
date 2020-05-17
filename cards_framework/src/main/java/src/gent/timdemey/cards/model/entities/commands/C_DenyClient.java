package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_DenyClient;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_DenyClient extends CommandBase
{
    public C_DenyClient()
    {
    }

    public C_DenyClient(P_DenyClient pl)
    {
        super(pl);
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    public void preExecute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            Services.get(IDialogService.class).ShowMessage("test",
                    "TEST: You were denied to join the game. Maybe the server is full.");
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
