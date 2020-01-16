package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.model.multiplayer.JoinMultiplayerGameData;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogData;
import gent.timdemey.cards.ui.dialogs.JoinMultiplayerGameDialogContent;

public class D_JoinGame extends CommandBase
{

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return state.getServerId() == null;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        JoinMultiplayerGameDialogContent content = new JoinMultiplayerGameDialogContent();
        DialogData<JoinMultiplayerGameData> data = Services.get(IDialogService.class)
                .ShowAdvanced(Loc.get("dialog_title_joingame"), null, content, DialogButtonType.BUTTONS_OK_CANCEL);

        if (data.closeType == DialogButtonType.Ok)
        {
            schedule(ContextType.UI, new C_Connect(data.payload.server.getInetAddress(),
                    data.payload.server.getTcpPort(), data.payload.playerName));
        }
    }

}
