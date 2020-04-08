package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_ClearServerList extends CommandBase
{

    public C_ClearServerList()
    {
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            state.getServers().clear();
        }
    }
}
