package gent.timdemey.cards.model.entities.commands;

import java.util.EnumSet;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogData;
import gent.timdemey.cards.ui.dialogs.LobbyDialogContent;

public class D_ShowLobby extends DialogCommandBase
{
    @Override
    protected CanExecuteResponse canShowDialog(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);

        if (state.getServerId() == null)
        {
            return CanExecuteResponse.no("State.ServerId is null");
        }
        if (state.getCardGame() != null)
        {
            return CanExecuteResponse.no("State.CardGame is not null");
        }

        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog(Context context, ContextType type, State state)
    {
        Server server = state.getServers().get(state.getServerId());

        String title = Loc.get(LocKey.DialogTitle_lobby, server.serverName);
        DialogData<Void> data = dialogServ.ShowAdvanced(title, null, new LobbyDialogContent(),
                EnumSet.of(DialogButtonType.Cancel));
        if (data.closeType == DialogButtonType.Cancel)
        {
            CommandBase cmd = new C_Disconnect(DisconnectReason.LocalPlayerLeft);
            schedule(ContextType.UI, cmd);
        }
    }

}
