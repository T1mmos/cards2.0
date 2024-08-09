package gent.timdemey.cards.model.entities.commands.dialogs;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.admin.C_StartServer;
import gent.timdemey.cards.model.entities.commands.CommandFactory;

import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.ui.panels.PanelOutData;
import gent.timdemey.cards.ui.panels.dialogs.mp.StartServerPanelData;

public class C_ShowStartServer extends DialogCommandBase<P_ShowStartServer>
{
    private final IFrameService _FrameService;
    private final CommandFactory _CommandFactory;
    
    public C_ShowStartServer (
        Container container,
        IFrameService frameService, 
        CommandFactory commandFactory,
        P_ShowStartServer parameters)
    {
        super(container, parameters);
        
        this._FrameService = frameService;
        this._CommandFactory = commandFactory;
    }
    
    @Override
    protected CanExecuteResponse canShowDialog()
    {
        if (_ContainerService.isInitialized(ContextType.Server))
        {
            return CanExecuteResponse.no("Server context already initialized");
        }
        if (_State.getGameState() != GameState.Disconnected)
        {
            return CanExecuteResponse.no("Cannot start a server while connected to a server, current GameState=" + _State.getGameState());
        }

        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog()
    {  
        String name = _Context.getReadOnlyState().getLocalName();     
        _FrameService.showPanel(PanelDescriptors.StartServer, name, this::onClose);
    }
    
    private void onClose(PanelOutData<StartServerPanelData> data)
    {
        if (data.closeType == PanelButtonDescriptors.Ok)
        {
            String localName = _State.getLocalName();
            int udpPort = _State.getConfiguration().getServerUdpPort();
            int tcpPort = _State.getConfiguration().getServerTcpPort();
            
            C_StartServer cmd_startServer =_CommandFactory.CreateStartServer(localName, data.data_out.srvname, data.data_out.srvmsg,
                    udpPort, tcpPort, data.data_out.autoconnect);

            schedule(ContextType.UI, cmd_startServer);
        }
    }
}
