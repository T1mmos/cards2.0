package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;

public class C_StopServer extends CommandBase
{
    public C_StopServer()
    {
        
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        if (state.getServerId() == null)
        {
            return CanExecuteResponse.no("There is no ServerId set");
        }
        
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            schedule(ContextType.Server, this);
            return;
        }
        
        // clean up
        state.setUdpServiceAnnouncer(null);       
        state.setTcpConnectionAccepter(null);     
        state.setTcpConnectionPool(null);
       
        // Drop the server context entirely
        IContextService ctxtServ = Services.get(IContextService.class);
        ctxtServ.drop(ContextType.Server);
    }

    @Override
    protected void undo(Context context, ContextType type, State state)
    {
        // TODO Auto-generated method stub
        super.undo(context, type, state);
    }
    
    @Override
    public String toDebugString()
    {
        return "";
    }
}
