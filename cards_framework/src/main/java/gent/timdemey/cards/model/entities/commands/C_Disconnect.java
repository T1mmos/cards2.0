package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Disconnect;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IFrameService;

/**
 * Leave the lobby and as such, fully disconnect from the server. All client
 * state is cleaned.
 * 
 * @author Tim
 *
 */
public class C_Disconnect extends CommandBase
{
    private final Loc _Loc;
    private final IFrameService _FrameService;

    public enum DisconnectReason
    {
        /** Lost connection to the server. */
        ConnectionLost,

        /** Local player launched the disconnect action. */
        LocalPlayerLeft,

        /** The lobby admin has left the server. */
        LobbyAdminLeft,

        /** The local player was kicked from the server */
        Kicked,
    }

    public final DisconnectReason reason;

    public C_Disconnect(
        Container container, IFrameService frameService, Loc loc,
        P_Disconnect parameters)
    {
        super(container, parameters);
        
        this._FrameService = frameService;
        this._Loc = loc;
        this.reason = parameters.reason;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        if (_State.getGameState() == GameState.Disconnected)
        {
            return CanExecuteResponse.no("GameState shouldn't be Disconnected");
        }
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        CheckContext(ContextType.UI);

        // clean all state
        _State.setTcpConnectionPool(null);
        _State.setUdpServiceRequester(null);
        _State.setGameState(GameState.Disconnected);
        _State.setCardGame(null);
        _State.getPlayers().clear();
        _State.getUDPServers().clear();
        _State.setLobbyAdminId(null);
        _State.setServer(null);
        _State.setServerMessage(null);
    }
    
    @Override
    public void onExecuted()
    {
        String title = null;
        String msg = null;        
        switch(reason)
        {
        case ConnectionLost:
            title = _Loc.get(LocKey.DialogTitle_connectionlost);
            msg = _Loc.get(LocKey.DialogMessage_connectionlost);
            break;
        case Kicked:
            title = _Loc.get(LocKey.DialogTitle_kicked);
            msg = _Loc.get(LocKey.DialogMessage_kicked);
            break;
        case LobbyAdminLeft:
            title = _Loc.get(LocKey.DialogTitle_lobbyAdminLeft);
            msg = _Loc.get(LocKey.DialogMessage_lobbyAdminLeft);
            break;
        case LocalPlayerLeft: // no dialog
        default:
            break;
        }
        
        if (title != null && msg != null)
        {
            _FrameService.showMessage(title, msg);
        }
    }
    
    @Override
    public String toDebugString()
    {
        return "";
    }
}
