package gent.timdemey.cards.entities;

class C_UDP_HelloServerStop extends ACommandPill {

    C_UDP_HelloServerStop() 
    {
    }

    @Override
    public CommandType getCommandType() 
    {
        return CommandType.Meta;
    }

    @Override
    public void execute()
    {
        ContextType type = getContextType();
        if (type != ContextType.Client)
        {
            throw new IllegalStateException();
        }        
        
        CommandProcessorClient cProcessor = getProcessorClient();
        if (cProcessor.serviceRequester == null)
        {
            throw new IllegalStateException("Already stopped the requesting service.");
        }
        
        cProcessor.serviceRequester.stop();
        cProcessor.serviceRequester = null;
    }

    @Override
    public void visitExecuted(IGameEventListener listener)
    {
        
    } 
}
