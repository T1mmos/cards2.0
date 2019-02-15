package gent.timdemey.cards.entities;

import java.util.UUID;

public class C_DropPlayer extends ACommandPill {

    static class CompactConverter extends ASerializer<C_DropPlayer>
    {
        @Override
        protected void write(SerializationContext<C_DropPlayer> sc) {
            writeUUID(sc, PROPERTY_PLAYER_ID, sc.src.id);
        }

        @Override
        protected C_DropPlayer read(DeserializationContext dc) 
        {
            UUID id = readUUID(dc, PROPERTY_PLAYER_ID);
            return new C_DropPlayer(id);
        }
                
    }
    
    final UUID id;
    
    C_DropPlayer(UUID id) {
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
            reschedule(ContextType.UI);
        }
        else 
        {
            getThreadContext().removePlayer(id);
            
            String ser = Json.send(CommandEnvelope.createCommandEnvelope(this));
            getThreadContext().getRemotePlayers().stream().forEach(c -> getProcessorServer().srv_tcp_connpool.getConnection(c.id).send(ser));
        }
    }

    @Override
    public void visitExecuted(IGameEventListener listener) {
        // TODO Auto-generated method stub
        
    }

}
