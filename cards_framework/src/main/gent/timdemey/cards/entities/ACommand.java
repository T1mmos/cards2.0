package gent.timdemey.cards.entities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;

public abstract class ACommand implements ICommand {


    /**
     * Set by the client processor to let this command complete the connection.
     */
    private volatile Object vd_tcpConnection = null;
    
    /**
     * Meta information about the command (count, who created it, ...).
     */
    private final MetaInfo metaInfo;

    /**
     * Re-creates a command using the supplied meta info. This is useful when constructing commands that 
     * arrived in a different form, meaning the command "already exists". In this case, meta info must
     * be extracted from the different form, and injected in this "new" command. A good example is 
     * where commands arrive over the wire, where the "different form" is the serialized form.
     */
    protected ACommand (MetaInfo metaInfo)
    {
        Preconditions.checkNotNull(metaInfo);
        
        this.metaInfo = metaInfo;
    }
    
    @Override
    public void setVolatileData(Object obj) 
    {
        vd_tcpConnection = obj; 
    }
    
    @Override
    public Object getVolatileData() {
        return vd_tcpConnection;
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
    
    @Override
    public void scheduleOn(ContextType contextType)
    {
        Services.get(IContextProvider.class).getContext(contextType).commandProcessor.schedule(this);
    }
    
    @Override
    public MetaInfo getMetaInfo() {
        return metaInfo;
    }
    
    @Override
    public String serialize() {
        return Json.send(new CommandEnvelope(this));
    }
}
