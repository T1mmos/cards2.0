package gent.timdemey.cards.entities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;

public abstract class ACommand implements ICommand {


    /**
     * Set by the client processor to let this command complete the connection.
     */
    

    /**
     * Re-creates a command using the supplied meta info. This is useful when constructing commands that 
     * arrived in a different form, meaning the command "already exists". In this case, meta info must
     * be extracted from the different form, and injected in this "new" command. A good example is 
     * where commands arrive over the wire, where the "different form" is the serialized form.
     */
    protected ACommand ()
    {
    }
    
    private volatile Object volatileData = null;    
    private volatile CommandEnvelope commandEnvelope = null;
        
    public void setVolatileData (Object obj)
    {
        volatileData = obj;
    }
    
    public Object getVolatileData()
    {
        return volatileData;
    }
    
    @Override
    public void setCommandEnvelope(CommandEnvelope envelope) 
    {
        commandEnvelope = envelope; 
    }
    
    @Override
    public CommandEnvelope getCommandEnvelope() 
    {
        return commandEnvelope;
    }
    
    protected final CommandProcessorUI getProcessorUI()
    {
        return (CommandProcessorUI) Services.get(IContextProvider.class).getContext(ContextType.UI).commandProcessor;
    }
    
    protected final CommandProcessorClient getProcessorClient()
    {
        return (CommandProcessorClient) Services.get(IContextProvider.class).getContext(ContextType.Client).commandProcessor;
    }
    
    protected final CommandProcessorServer getProcessorServer()
    {
        return (CommandProcessorServer) Services.get(IContextProvider.class).getContext(ContextType.Server).commandProcessor;
    }
    
    /**
     * Full thread context. (Card game holder, current processor etc.)
     * @return
     */
    protected final ContextFull getThreadContext()
    {
        return Services.get(IContextProvider.class).getThreadContext();
    }
    
    protected final ContextType getContextType()
    {
        return getThreadContext().getContextType();
    }
    /*
    final CommandEnvelope toNewEnvelope()
    {
        return new CommandEnvelope(this);
    }   
    */
    final void schedule (ContextType contextType)
    {
        Preconditions.checkState(commandEnvelope == null);
        Services.get(IContextProvider.class).getContext(contextType).commandProcessor.schedule(this);
    }
    
    protected final void reschedule (ContextType contextType)
    {
        Preconditions.checkNotNull(commandEnvelope);
        commandEnvelope.reschedule(contextType);
    }
    
}
