package gent.timdemey.cards.model.entities.commands;

import java.util.EnumSet;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogData;
import gent.timdemey.cards.ui.dialogs.LobbyDialogContent;

public class D_Lobby extends DialogCommandBase
{
    @Override
    protected boolean canShowDialog(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);

        return state.getServerId() != null && state.getCardGame() == null;
    }

    @Override
    protected void showDialog(Context context, ContextType type, State state)
    {
        Server server = state.getServers().get(state.getServerId());

        String title = Loc.get(LocKey.DialogTitle_lobby, server.serverName);
        DialogData<Void> data = dialogServ.ShowAdvanced(title, null, new LobbyDialogContent(), EnumSet.of(DialogButtonType.Cancel));
        if(data.closeType == DialogButtonType.Cancel)
        {
            // must drop connections
            CommandBase cmd = new C_Disconnect();
            schedule(ContextType.UI, cmd);
        }
    }

}
