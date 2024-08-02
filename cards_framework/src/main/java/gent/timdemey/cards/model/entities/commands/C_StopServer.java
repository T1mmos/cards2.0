package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_StopServer;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;

public class C_StopServer extends CommandBase
{    
    public C_StopServer(
        IContextService contextService, State state,
        P_StopServer parameters)
    {
        super(contextService, state, parameters);
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        if (_State.getServerId() == null)
        {
            return CanExecuteResponse.no("There is no ServerId set");
        }
        
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type)
    {
        if (type == ContextType.UI)
        {
            schedule(ContextType.Server, this);
            return;
        }
        
        // clean up
        _State.setUdpServiceAnnouncer(null);       
        _State.setTcpConnectionAccepter(null);     
        _State.setTcpConnectionPool(null);
       
        // Drop the server context entirely
        _ContextService.drop(ContextType.Server);
    }

    @Override
    protected void undo(Context context, ContextType type)
    {
        // TODO Auto-generated method stub
        super.undo(context, type);
    }
    
    @Override
    public String toDebugString()
    {
        return "";
    }
}
