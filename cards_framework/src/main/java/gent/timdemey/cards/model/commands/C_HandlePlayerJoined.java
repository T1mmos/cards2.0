package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.model.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_HandlePlayerJoined extends CommandBase
{
    final Player player;

    C_HandlePlayerJoined(Player player)
    {
        this.player = player;
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
            state.getPlayers().add(player);
        }
        else if (contextType == ContextType.Client)
        {
            state.getPlayers().add(player);
            reschedule(ContextType.UI);
        }
        else
        {
            throw new IllegalStateException();
        }
    }
}
