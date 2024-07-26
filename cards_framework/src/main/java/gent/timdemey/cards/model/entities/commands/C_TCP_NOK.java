package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import java.util.UUID;

public class C_TCP_NOK extends CommandBase
{
    private Loc _Loc;
    private IFrameService _FrameService;
    private final CommandFactory _CommandFactory;
    public enum TcpNokReason
    {
        LobbyFull
    }
    
    public final TcpNokReason reason;
    
    C_TCP_NOK (
        IContextService contextService, IFrameService frameService, CommandFactory commandFactory, Loc loc, 
        UUID id, TcpNokReason reason)
    {
        super(contextService, id);
        
        this._FrameService = frameService;
        this._CommandFactory = commandFactory;
        this._Loc = loc;
        this.reason = reason;
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
            title = _Loc.get(LocKey.DialogTitle_lobbyFull);
            msg = _Loc.get(LocKey.DialogMessage_lobbyFull);
            break;        
        default:
            break;
        }
                
        if (title != null && msg != null)
        {
            _FrameService.showMessage(title, msg, c -> ShowServerBrowser());
        }
        else 
        {
            ShowServerBrowser();
        }
    }
    
    private void ShowServerBrowser()
    {
        D_Connect cmd_connect = _CommandFactory.ShowDialog_Connect();
        schedule(ContextType.UI, cmd_connect);
    }
}
