package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_LeaveGame extends CommandBase
{
    public C_LeaveGame()
    {
        
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return state.getCardGame() != null;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toDebugString()
    {
        return "";
    }

}
