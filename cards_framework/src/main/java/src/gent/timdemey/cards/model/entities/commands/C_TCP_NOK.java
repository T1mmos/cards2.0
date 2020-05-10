package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.payload.P_TCP_NOK;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

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
    protected void preExecute(Context context, ContextType type, State state)
    {
        state.setTcpConnectionPool(null);        
    }
}
