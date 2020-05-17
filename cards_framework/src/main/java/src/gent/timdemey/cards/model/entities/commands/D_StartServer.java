package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogData;
import gent.timdemey.cards.ui.dialogs.StartServerDialogContent;
import gent.timdemey.cards.ui.dialogs.StartServerDialogData;

public class D_StartServer extends DialogCommandBase
{
    @Override
    protected CanExecuteResponse canShowDialog(Context context, ContextType type, State state)
    {
        if (Services.get(IContextService.class).isInitialized(ContextType.Server))
        {
            return CanExecuteResponse.no("Server context already initialized");
        }
        if (state.getGameState() != GameState.NotConnected)
        {
            return CanExecuteResponse.no("Cannot start a server while connected to a server, current GameState=" + state.getGameState());
        }

        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog(Context context, ContextType type, State state)
    {
        StartServerDialogContent content = new StartServerDialogContent(state.getLocalName());
        IDialogService diagServ = Services.get(IDialogService.class);
        String title = Loc.get(LocKey.DialogTitle_creategame);
        DialogData<StartServerDialogData> data = diagServ.ShowAdvanced(title, null, content, DialogButtonType.BUTTONS_OK_CANCEL);

        if (data.closeType == DialogButtonType.Ok)
        {
            C_StartServer cmd_startServer = new C_StartServer(state.getLocalId(), data.payload.playerName, data.payload.srvname, data.payload.srvmsg,
                    data.payload.udpport, data.payload.tcpport, data.payload.autoconnect);

            schedule(ContextType.UI, cmd_startServer);
        }
    }
}
