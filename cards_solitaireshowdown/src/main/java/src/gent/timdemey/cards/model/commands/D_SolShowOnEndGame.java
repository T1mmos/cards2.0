package gent.timdemey.cards.model.commands;

import java.util.UUID;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
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
        String title, msg;
        if (state.getLocalId().equals(winnerId))
        {
            title = Loc.get(LocKey.DialogTitle_youwin);
            msg = Loc.get(LocKey.DialogMessage_youwin);    
        }
        else
        {
            title = Loc.get(LocKey.DialogTitle_youlose);
            msg = Loc.get(LocKey.DialogMessage_youlose);
        }
        
        dialogServ.ShowMessage(title, msg);
    }
}
