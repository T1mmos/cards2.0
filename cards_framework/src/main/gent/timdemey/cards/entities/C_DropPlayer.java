package gent.timdemey.cards.entities;

import java.util.UUID;

public class C_DropPlayer extends ACommandPill {

    final UUID id;
    
    C_DropPlayer(MetaInfo info, UUID id) {
        super(info);
        this.id = id;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.Meta;
    }

    @Override
    public void execute() 
    {
        ContextType contextType = getContextType();
        if (contextType == ContextType.UI)
        {
            getThreadContext().removePlayer(id);            
        }
        else if (contextType == ContextType.Client)
        {
            getThreadContext().removePlayer(id);
            scheduleOn(ContextType.UI);
        }
        else 
        {
            getThreadContext().removePlayer(id);
            
            String ser = Json.send(new CommandEnvelope(this));
            getThreadContext().getRemotePlayers().stream().forEach(c -> getProcessorServer().srv_tcp_connpool.getConnection(c.id).send(ser));
        }
    }

    @Override
    public void visitExecuted(IGameEventListener listener) {
        // TODO Auto-generated method stub
        
    }

}
