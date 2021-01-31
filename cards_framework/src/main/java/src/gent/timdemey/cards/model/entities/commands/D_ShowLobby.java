package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.panels.PanelOutData;
import gent.timdemey.cards.services.panels.dialogs.mp.LobbyPanelData;

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
        Server server = state.getServer();
     
        IFrameService frameServ = Services.get(IFrameService.class);
        LobbyPanelData payload = new LobbyPanelData(server.serverName);
        frameServ.showPanel(PanelDescriptors.Lobby, payload, this::onClose);        
    }

    private void onClose(PanelOutData<Void> outData)
    {
        if (outData.closeType == PanelButtonDescriptors.Cancel)
        {
            CommandBase cmd = new C_Disconnect(DisconnectReason.LocalPlayerLeft);
            schedule(ContextType.UI, cmd);
        }
    }
}
