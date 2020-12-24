package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IDialogService;
import gent.timdemey.cards.services.panels.PanelButtonType;
import gent.timdemey.cards.services.panels.PanelOutData;
import gent.timdemey.cards.services.panels.mp.StartServerPanelManager;
import gent.timdemey.cards.services.panels.mp.StartServerPanelData;

public class D_StartServer extends DialogCommandBase
{
    @Override
    protected CanExecuteResponse canShowDialog(Context context, ContextType type, State state)
    {
        if (Services.get(IContextService.class).isInitialized(ContextType.Server))
        {
            return CanExecuteResponse.no("Server context already initialized");
        }
        if (state.getGameState() != GameState.Disconnected)
        {
            return CanExecuteResponse.no("Cannot start a server while connected to a server, current GameState=" + state.getGameState());
        }

        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog(Context context, ContextType type, State state)
    {
        StartServerPanelManager content = new StartServerPanelManager(state.getLocalName());
        IDialogService diagServ = Services.get(IDialogService.class);
        String title = Loc.get(LocKey.DialogTitle_creategame);
        PanelOutData<StartServerPanelData> data = diagServ.ShowAdvanced(title, null, content);

        if (data.closeType == PanelButtonType.Ok)
        {
            C_StartServer cmd_startServer = new C_StartServer(state.getLocalId(), data.data_out.playerName, data.data_out.srvname, data.data_out.srvmsg,
                    data.data_out.udpport, data.data_out.tcpport, data.data_out.autoconnect);

            schedule(ContextType.UI, cmd_startServer);
        }
    }
}
