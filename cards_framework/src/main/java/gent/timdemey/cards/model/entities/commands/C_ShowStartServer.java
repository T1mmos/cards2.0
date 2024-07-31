package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;


import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_ShowStartServer;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.ui.panels.PanelOutData;
import gent.timdemey.cards.ui.panels.dialogs.mp.StartServerPanelData;

public class C_ShowStartServer extends DialogCommandBase
{
    private final IFrameService _FrameService;
    private final CommandFactory _CommandFactory;
    
    public C_ShowStartServer (
        IContextService contextService, 
        IFrameService frameService, 
        CommandFactory commandFactory,
        P_ShowStartServer parameters)
    {
        super(contextService, parameters);
        
        this._FrameService = frameService;
        this._CommandFactory = commandFactory;
    }
    
    @Override
    protected CanExecuteResponse canShowDialog(Context context, ContextType type, State state)
    {
        if (_ContextService.isInitialized(ContextType.Server))
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
        String name = _ContextService.getThreadContext().getReadOnlyState().getLocalName();     
        _FrameService.showPanel(PanelDescriptors.StartServer, name, this::onClose);
    }
    
    private void onClose(PanelOutData<StartServerPanelData> data)
    {
        if (data.closeType == PanelButtonDescriptors.Ok)
        {
            ReadOnlyState state = _ContextService.getThreadContext().getReadOnlyState();
            UUID localId = state.getLocalId();
            String localName = state.getLocalName();
            int udpPort = state.getConfiguration().getServerUdpPort();
            int tcpPort = state.getConfiguration().getServerTcpPort();
            
            C_StartServer cmd_startServer =_CommandFactory.CreateStartServer(localId, localName, data.data_out.srvname, data.data_out.srvmsg,
                    udpPort, tcpPort, data.data_out.autoconnect);

            schedule(ContextType.UI, cmd_startServer);
        }
    }
}
