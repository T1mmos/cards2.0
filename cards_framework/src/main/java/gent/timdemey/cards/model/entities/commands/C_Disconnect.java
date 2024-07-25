package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Disconnect;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
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

    public C_Disconnect(DisconnectReason reason)
    {
        this.reason = reason;
    }

    public C_Disconnect(P_Disconnect pl)
    {
        super(pl);

        this.reason = pl.reason;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        if (state.getGameState() == GameState.Disconnected)
        {
            return CanExecuteResponse.no("GameState shouldn't be Disconnected");
        }
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);

        // clean all state
        state.setTcpConnectionPool(null);
        state.setUdpServiceRequester(null);
        state.setGameState(GameState.Disconnected);
        state.setCardGame(null);
        state.getPlayers().clear();
        state.getUDPServers().clear();
        state.setLobbyAdminId(null);
        state.setServer(null);
        state.setServerMessage(null);
    }
    
    @Override
    public void onExecuted()
    {
        String title = null;
        String msg = null;        
        switch(reason)
        {
        case ConnectionLost:
            title = Loc.get(LocKey.DialogTitle_connectionlost);
            msg = Loc.get(LocKey.DialogMessage_connectionlost);
            break;
        case Kicked:
            title = Loc.get(LocKey.DialogTitle_kicked);
            msg = Loc.get(LocKey.DialogMessage_kicked);
            break;
        case LobbyAdminLeft:
            title = Loc.get(LocKey.DialogTitle_lobbyAdminLeft);
            msg = Loc.get(LocKey.DialogMessage_lobbyAdminLeft);
            break;
        case LocalPlayerLeft: // no dialog
        default:
            break;
        }
        
        if (title != null && msg != null)
        {
            Services.get(IFrameService.class).showMessage(title, msg);
        }
    }
    
    @Override
    public String toDebugString()
    {
        return "";
    }
}
