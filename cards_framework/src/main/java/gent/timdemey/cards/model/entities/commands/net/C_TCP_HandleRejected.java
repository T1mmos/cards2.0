package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;

public class C_TCP_HandleRejected extends CommandBase<P_TCP_HandleRejected>
{
    public enum TcpNokReason
    {
        LobbyFull
    }
    
    public final TcpNokReason reason;
    
    public C_TCP_HandleRejected (
        Container container, 
        P_TCP_HandleRejected parameters)
    {
        super(container, parameters);
        
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
    }
}
