package gent.timdemey.cards.model.commands;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_SolShowPush extends CommandBase
{

    public C_SolShowPush(UUID dstCardStackId, List<UUID> toTransferIds)
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        // TODO Auto-generated method stub
        
    }

}
