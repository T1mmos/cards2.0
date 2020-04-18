package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogData;
import gent.timdemey.cards.ui.dialogs.CreateMultiplayerGameDialogContent;

public class D_CreateGame extends DialogCommandBase
{
    @Override
    protected boolean canShowDialog(Context context, ContextType type, State state)
    {
        return !Services.get(IContextService.class).isInitialized(ContextType.Server);
    }
    
    @Override
    protected void showDialog(Context context, ContextType type, State state)
    {
        CreateMultiplayerGameDialogContent content = new CreateMultiplayerGameDialogContent();
        IDialogService diagServ = Services.get(IDialogService.class);
        String title = Loc.get(LocKey.DialogTitle_creategame);
        DialogData<C_CreateServer> data = diagServ.ShowAdvanced(title, null, content, DialogButtonType.BUTTONS_OK_CANCEL);

        if(data.closeType == DialogButtonType.Ok)
        {
            C_CreateServer command = data.payload;
            schedule(ContextType.UI, command);
        }
    }
}
