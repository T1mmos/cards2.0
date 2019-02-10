package gent.timdemey.cards.entities;

public class C_Disconnect extends ACommandPill {

    C_Disconnect(MetaInfo info) {
        super(info);
        // TODO Auto-generated constructor stub
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.Meta;
    }

    @Override
    public void execute() {
        ContextType contextType = getContextType();
        
        if (contextType == ContextType.Client)
        {
            getProcessorClient().connPool.closeAllConnections();
            
            getThreadContext().removeRemotes();
            getThreadContext().setServerId(null);
        }
        else if (contextType == ContextType.UI)
        {
            getThreadContext().removeRemotes();
            getThreadContext().setServerId(null);
            
            scheduleOn(ContextType.Client);
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public void visitExecuted(IGameEventListener listener) {
        
    }

}
