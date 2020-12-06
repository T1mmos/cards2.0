package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.readonlymodel.ReadOnlyUDPServer;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogOutData;
import gent.timdemey.cards.services.interfaces.IDialogService;
import gent.timdemey.cards.ui.dialogs.JoinMultiplayerGameData;
import gent.timdemey.cards.ui.dialogs.JoinMultiplayerGameDialogContent;

public class D_Connect extends DialogCommandBase
{
    @Override
    protected CanExecuteResponse canShowDialog(Context context, ContextType type, State state)
    {
        if (state.getGameState() != GameState.Disconnected)
        {
            return CanExecuteResponse.no("GameState should be Disconnected but is: " + state.getGameState());
        }
        if (state.getServerId() != null)
        {
            return CanExecuteResponse.no("State.ServerId is not null: " + state.getServerId());
        }

        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog(Context context, ContextType type, State state)
    {
        JoinMultiplayerGameDialogContent content = new JoinMultiplayerGameDialogContent();
        IDialogService diagServ = Services.get(IDialogService.class);
        String title = Loc.get(LocKey.DialogTitle_joingame);
        DialogOutData<JoinMultiplayerGameData> data = diagServ.ShowAdvanced(title, null, content);

        if (data.closeType == DialogButtonType.Ok)
        {
            ReadOnlyUDPServer udpServer = data.data_out.server;
            Server server = udpServer.getServer();
            C_Connect cmd = new C_Connect(state.getLocalId(), server.id, server.inetAddress,
                    server.tcpport, server.serverName, data.data_out.playerName);
            schedule(ContextType.UI, cmd);
        }
    }
}
