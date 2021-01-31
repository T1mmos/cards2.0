package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.readonlymodel.ReadOnlyUDPServer;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.panels.PanelOutData;
import gent.timdemey.cards.services.panels.dialogs.mp.JoinMPGamePanelData;

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
        IFrameService frameServ = Services.get(IFrameService.class);  
        frameServ.showPanel(PanelDescriptors.Connect, null, this::onClose);     
    }
    
    private void onClose(PanelOutData<JoinMPGamePanelData> data)
    {
        IContextService ctxtServ = Services.get(IContextService.class);
        UUID localId = ctxtServ.getThreadContext().getReadOnlyState().getLocalId();
        
        if (data.closeType == PanelButtonDescriptors.Ok)
        {
            ReadOnlyUDPServer udpServer = data.data_out.server;
            Server server = udpServer.getServer();
            C_Connect cmd = new C_Connect(localId, server.id, server.inetAddress,
                    server.tcpport, server.serverName, data.data_out.playerName);
            schedule(ContextType.UI, cmd);
        }
    }
}
