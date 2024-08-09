package gent.timdemey.cards.model.entities.commands.admin;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.services.context.ContextType;

public class C_StopServer extends CommandBase<P_StopServer>
{    
    public C_StopServer(
        Container container,
        P_StopServer parameters)
    {
        super(container, parameters);
    }
    
    @Override
    public CanExecuteResponse canExecute()
    {
        if (_State.getServer() == null)
        {
            return CanExecuteResponse.no("There is no ServerId set");
        }
        
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        if (_ContextType == ContextType.UI)
        {
            schedule(ContextType.Server, this);
            return;
        }
        
        // clean up
        _State.setUdpServiceAnnouncer(null);       
        _State.setTcpConnectionAccepter(null);     
        _State.setTcpConnectionPool(null);
       
        // Drop the server context entirely
        _ContainerService.drop(ContextType.Server);
    }

    @Override
    public void undo()
    {
        // TODO Auto-generated method stub
        super.undo();
    }
    
    @Override
    public String toDebugString()
    {
        return "";
    }
}
