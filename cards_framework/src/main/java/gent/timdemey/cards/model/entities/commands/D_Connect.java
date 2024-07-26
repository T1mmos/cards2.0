package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;


import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.readonlymodel.ReadOnlyUDPServer;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.ui.panels.PanelOutData;
import gent.timdemey.cards.ui.panels.dialogs.mp.JoinMPGamePanelData;

public class D_Connect extends DialogCommandBase
{

    private final IFrameService _FrameService;
    private final CommandFactory _CommandFactory;
    public D_Connect(
        IContextService contextService, IFrameService frameService, CommandFactory commandFactory,
        UUID id)
    {
        super(contextService, id);
        
        this._FrameService = frameService;
        this._CommandFactory = commandFactory;
    }
    
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
        _FrameService.showPanel(PanelDescriptors.Connect, null, this::onClose);     
    }
    
    private void onClose(PanelOutData<JoinMPGamePanelData> data)
    {
        UUID localId = _ContextService.getThreadContext().getReadOnlyState().getLocalId();
        
        if (data.closeType == PanelButtonDescriptors.Ok)
        {
            ReadOnlyUDPServer udpServer = data.data_out.server;
            ServerTCP server = udpServer.getServer();
            C_Connect cmd = _CommandFactory.CreateConnect(localId, server.id, server.inetAddress,
                    server.tcpport, server.serverName, data.data_out.playerName);
            schedule(ContextType.UI, cmd);
        }
    }
}
