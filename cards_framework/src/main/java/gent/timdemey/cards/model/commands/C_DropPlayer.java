package gent.timdemey.cards.model.commands;

import java.util.UUID;

import gent.timdemey.cards.model.commands.CommandEnvelope;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_DropPlayer extends CommandBase {

    private final UUID playerId;
    
    C_DropPlayer(UUID playerId) 
    {
        this.playerId = playerId;
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }
    
    @Override
    public void execute(Context context, ContextType contextType, State state) 
    {
        if (contextType == ContextType.UI)
        {
            getThreadContext().removePlayer(playerId);            
        }
        else if (contextType == ContextType.Client)
        {
            getThreadContext().removePlayer(playerId);
            reschedule(ContextType.UI);
        }
        else 
        {
            getThreadContext().removePlayer(playerId);
            
            String ser = Json.send(CommandEnvelope.createCommandEnvelope(this));
            getThreadContext().getRemotePlayers().stream().forEach(c -> getProcessorServer().srv_tcp_connpool.getConnection(c.id).send(ser));
        }
    }

}
