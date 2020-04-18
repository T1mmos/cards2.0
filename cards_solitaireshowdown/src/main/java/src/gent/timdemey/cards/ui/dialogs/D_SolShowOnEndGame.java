package gent.timdemey.cards.ui.dialogs;

import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.DialogCommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class D_SolShowOnEndGame extends DialogCommandBase
{
    public final UUID winnerId;
    
    public D_SolShowOnEndGame(UUID winnerId)
    {
        this.winnerId = winnerId;
    }

    @Override
    protected boolean canShowDialog(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void showDialog(Context context, ContextType type, State state)
    {
        
        dialogServ.ShowAdvanced(title, data, dialogContent, closeType)
    }
}
