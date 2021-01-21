package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.panels.PanelButtonType;
import gent.timdemey.cards.services.panels.PanelOutData;
import gent.timdemey.cards.services.panels.dialogs.mp.StartServerPanelData;

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
        IFrameService frameServ = Services.get(IFrameService.class);
        IContextService ctxtServ = Services.get(IContextService.class);        
        String name = ctxtServ.getThreadContext().getReadOnlyState().getLocalName();     
        frameServ.showPanel(PanelDescriptors.START_SERVER, name, this::onClose);
    }
    
    private void onClose(PanelOutData<StartServerPanelData> data)
    {
        if (data.closeType == PanelButtonType.Ok)
        {
            IContextService ctxtServ = Services.get(IContextService.class);
            ReadOnlyState state = ctxtServ.getThreadContext().getReadOnlyState();
            UUID localId = state.getLocalId();
            String localName = state.getLocalName();
            
            C_StartServer cmd_startServer = new C_StartServer(localId, localName, data.data_out.srvname, data.data_out.srvmsg,
                    data.data_out.udpport, data.data_out.tcpport, data.data_out.autoconnect);

            schedule(ContextType.UI, cmd_startServer);
        }
    }
}
