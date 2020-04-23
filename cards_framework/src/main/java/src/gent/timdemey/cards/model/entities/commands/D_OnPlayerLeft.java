package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class D_OnPlayerLeft extends DialogCommandBase
{
    public final String playerName;
    
    public D_OnPlayerLeft (String playerName)
    {
        this.playerName = playerName;
    }
    
    @Override
    protected boolean canShowDialog(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void showDialog(Context context, ContextType type, State state)
    {
        String title = Loc.get(LocKey.DialogTitle_playerleft);
        String msg = Loc.get(LocKey.DialogMessage_playerleft, playerName);
        Services.get(IDialogService.class).ShowMessage(title, msg);
    }
}
