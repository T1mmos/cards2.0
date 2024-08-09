package gent.timdemey.cards.model.entities.commands.dialogs;


import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.net.C_TCP_ClientDisconnect.DisconnectReason;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptors;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.ui.panels.PanelOutData;
import gent.timdemey.cards.ui.panels.dialogs.mp.LobbyPanelData;

public class C_ShowLobby extends DialogCommandBase<P_ShowLobby>
{
    private final IFrameService _FrameService;
    private final CommandFactory _CommandFactory;
    
    public C_ShowLobby (
        Container container, IFrameService frameService, CommandFactory commandFactory,
        P_ShowLobby parameters)
    {
        super(container, parameters);
        
        this._FrameService = frameService;
        this._CommandFactory = commandFactory;
    }
            
    @Override
    protected CanExecuteResponse canShowDialog()
    {
        CheckContext(ContextType.UI);

        if (_State.getServer().id == null)
        {
            return CanExecuteResponse.no("State.ServerId is null");
        }
        if (_State.getCardGame() != null)
        {
            return CanExecuteResponse.no("State.CardGame is not null");
        }

        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog()
    {
        ServerTCP server = _State.getServer();
     
        LobbyPanelData payload = new LobbyPanelData(server.serverName);
        _FrameService.showPanel(PanelDescriptors.Lobby, payload, this::onClose);        
    }

    private void onClose(PanelOutData<Void> outData)
    {
        if (outData.closeType == PanelButtonDescriptors.Cancel)
        {
            CommandBase cmd = _CommandFactory.CreateTCPClientDisconnect(DisconnectReason.LocalPlayerLeft);
            schedule(ContextType.UI, cmd);
        }
    }
}
