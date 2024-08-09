package gent.timdemey.cards.model.entities.commands.net;


import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.dialogs.C_ShowConnect;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IFrameService;

public class C_TCP_HandleRejected extends CommandBase<P_TCP_HandleRejected>
{
    private Loc _Loc;
    private IFrameService _FrameService;
    private final CommandFactory _CommandFactory;
    public enum TcpNokReason
    {
        LobbyFull
    }
    
    public final TcpNokReason reason;
    
    public C_TCP_HandleRejected (
        Container container, IFrameService frameService, CommandFactory commandFactory, Loc loc, 
        P_TCP_HandleRejected parameters)
    {
        super(container, parameters);
        
        this._FrameService = frameService;
        this._CommandFactory = commandFactory;
        this._Loc = loc;
        this.reason = parameters.reason;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        _State.setServer(null);
        _State.setTcpConnectionPool(null);  
        
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
        C_ShowConnect cmd_connect = _CommandFactory.ShowDialog_Connect();
        schedule(ContextType.UI, cmd_connect);
    }
}
