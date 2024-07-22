package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;


import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IFrameService;

public class D_OnEndGame extends DialogCommandBase
{
    public final UUID winnerId;
    
    public D_OnEndGame(UUID winnerId)
    {
        this.winnerId = winnerId;
    }

    @Override
    protected CanExecuteResponse canShowDialog(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();
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
        
        IFrameService frameServ = Services.get(IFrameService.class);
        frameServ.showMessage(title, msg);
    }
}
