package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.exec.ServerContext;

public class CreateServerCommand extends CommandBase
{

    @Override
    protected boolean canExecuteCore(Context state)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void executeCore(Context state)
    {
        ServerContext srvCtxt = Services.get(ServerContext.class);
        srvCtxt.setLocalName(request.srvname);            
        srvCtxt.setServerMessage(request.srvmsg);
        srvCtxt.setServerId(srvCtxt.getLocalId());    
    }

    @Override
    protected void rollbackCore(Context state)
    {
        // TODO Auto-generated method stub
        
    }

}
