package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.payload.P_Move;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public abstract class C_Move extends CommandBase
{
    public final UUID srcCardStackId;
    public final UUID dstCardStackId;
    public final UUID cardId;
    
    public C_Move (UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        this.srcCardStackId = srcCardStackId;
        this.dstCardStackId = dstCardStackId;
        this.cardId = cardId;
    }
    
    public C_Move (P_Move pl)
    {
        super(pl);
        
        this.srcCardStackId = pl.srcCardStackId;
        this.dstCardStackId = pl.dstCardStackId;
        this.cardId = pl.cardId;
    }

    public final boolean isSyncable()
    {
        return true;  
    }
    
    @Override
    protected abstract void undo(Context context, ContextType type, State state);
    
    @Override
    protected abstract boolean canUndo(Context context, ContextType type, State state);
}
