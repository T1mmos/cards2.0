package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogData;
import gent.timdemey.cards.ui.dialogs.CreateMultiplayerGameDialogContent;

public class D_CreateGame extends CommandBase
{

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return Services.get(IContextService.class).isInitialized(ContextType.Server);
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CreateMultiplayerGameDialogContent content = new CreateMultiplayerGameDialogContent();
        DialogData<C_CreateServer> data = Services.get(IDialogService.class)
                .ShowAdvanced(Loc.get("dialog_title_creategame"), null, content, DialogButtonType.BUTTONS_OK_CANCEL);

        if (data.closeType == DialogButtonType.Ok)
        {
            C_CreateServer command = data.payload;
            schedule(ContextType.UI, command);
        }
    }

}
