package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.readonlymodel.ReadOnlyServer;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogData;
import gent.timdemey.cards.ui.dialogs.JoinMultiplayerGameData;
import gent.timdemey.cards.ui.dialogs.JoinMultiplayerGameDialogContent;

public class D_Connect extends DialogCommandBase
{
    @Override
    protected boolean canShowDialog(Context context, ContextType type, State state)
    {
        return state.getServerId() == null;
    }

    @Override
    protected void showDialog(Context context, ContextType type, State state)
    {
        JoinMultiplayerGameDialogContent content = new JoinMultiplayerGameDialogContent();
        IDialogService diagServ = Services.get(IDialogService.class);
        String title = Loc.get(LocKey.DialogTitle_joingame);
        DialogData<JoinMultiplayerGameData> data = diagServ.ShowAdvanced(title, null, content,
                DialogButtonType.BUTTONS_OK_CANCEL);

        if (data.closeType == DialogButtonType.Ok)
        {
            ReadOnlyServer server = data.payload.server;
            C_Connect cmd = new C_Connect(state.getLocalId(), server.getId(), server.getInetAddress(),
                    server.getTcpPort(), data.payload.server.getServerName(), data.payload.playerName);
            schedule(ContextType.UI, cmd);
        }
    }
}
