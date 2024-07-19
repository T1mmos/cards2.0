package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_TCP_NOK;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IFrameService;
import java.util.function.Consumer;

public class C_TCP_NOK extends CommandBase
{
    public enum TcpNokReason
    {
        LobbyFull
    }
    
    public final TcpNokReason reason;
    
    public C_TCP_NOK (TcpNokReason reason)
    {
        this.reason = reason;
    }
    
    public C_TCP_NOK (P_TCP_NOK pl)
    {
        super(pl);
        this.reason = pl.reason;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        state.setServer(null);
        state.setTcpConnectionPool(null);  
        
        String title = null;
        String msg = null;      
        switch(reason)
        {
        case LobbyFull:
            title = Loc.get(LocKey.DialogTitle_lobbyFull);
            msg = Loc.get(LocKey.DialogMessage_lobbyFull);
            break;        
        default:
            break;
        }
                
        if (title != null && msg != null)
        {
            Services.get(IFrameService.class).showMessage(title, msg, c -> ShowServerBrowser());
        }
        else 
        {
            ShowServerBrowser();
        }
    }
    
    private void ShowServerBrowser()
    {
        D_Connect cmd_connect = new D_Connect();
        schedule(ContextType.UI, cmd_connect);
    }
}
