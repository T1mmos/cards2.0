package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.payload.P_Reject;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_Reject extends CommandBase
{
    public final UUID rejectedCommandId;
    
    public C_Reject (UUID rejectedCommandId)
    {
        this.rejectedCommandId = rejectedCommandId;
    }
    
    public C_Reject (P_Reject pl)
    {
        super(pl);
        
        this.rejectedCommandId = pl.rejectedCommandId;
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return type == ContextType.Client;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.UI, ContextType.Server);
        
        forward(type, state);
    }
}
