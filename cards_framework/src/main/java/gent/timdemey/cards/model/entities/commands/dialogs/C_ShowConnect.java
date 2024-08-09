package gent.timdemey.cards.model.entities.commands.dialogs;

import gent.timdemey.cards.model.entities.commands.net.C_TCP_ClientConnect;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandFactory;

import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.readonlymodel.ReadOnlyUDPServer;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.ui.panels.PanelOutData;
import gent.timdemey.cards.ui.panels.dialogs.mp.JoinMPGamePanelData;

public class C_ShowConnect extends DialogCommandBase<P_ShowConnect>
{
    private final IFrameService _FrameService;
    private final CommandFactory _CommandFactory;
    
    public C_ShowConnect(
        Container container, IFrameService frameService, CommandFactory commandFactory, 
        P_ShowConnect parameters)
    {
        super(container, parameters);
        
        this._FrameService = frameService;
        this._CommandFactory = commandFactory;
    }
    
    @Override
    protected CanExecuteResponse canShowDialog()
    {
        if (_State.getGameState() != GameState.Disconnected)
        {
            return CanExecuteResponse.no("GameState should be Disconnected but is: " + _State.getGameState());
        }
        if (_State.getServer() != null)
        {
            return CanExecuteResponse.no("State.ServerId is not null: " + _State.getServer().id);
        }

        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog()
    {        
        _FrameService.showPanel(PanelDescriptors.Connect, null, this::onClose);     
    }
    
    private void onClose(PanelOutData<JoinMPGamePanelData> data)
    {        
        if (data.closeType == PanelButtonDescriptors.Ok)
        {
            ReadOnlyUDPServer udpServer = data.data_out.server;
            ServerTCP server = udpServer.getServer();
            C_TCP_ClientConnect cmd = _CommandFactory.CreateTCPClientConnect(server.id, server.inetAddress,
                    server.tcpport, server.serverName, data.data_out.playerName);
            schedule(ContextType.UI, cmd);
        }
    }
}
