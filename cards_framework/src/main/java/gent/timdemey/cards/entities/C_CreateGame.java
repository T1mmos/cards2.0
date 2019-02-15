package gent.timdemey.cards.entities;

import gent.timdemey.cards.multiplayer.CreateServerInfo;

class C_CreateGame extends ACommandServer {

    private final CreateServerInfo srvinfo;
    
    protected C_CreateGame(CreateServerInfo srvinfo) 
    {
        this.srvinfo = srvinfo;
    }

    @Override
    public void execute() 
    {
        ContextType contextType = getContextType();
        if (contextType == ContextType.Server)
        {
            getThreadContext().setLocalName(srvinfo.srvname);            
            getThreadContext().setServerMessage(srvinfo.srvmsg);
            getThreadContext().setServerId(getThreadContext().getLocalId());
            getProcessorServer().createServer(srvinfo);
        }
        else 
        {
            throw new IllegalStateException();
        }
    }

}
