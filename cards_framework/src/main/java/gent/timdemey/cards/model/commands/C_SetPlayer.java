package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.model.other.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public final class C_SetPlayer extends CommandBase
{
    private final String name;
    
    public C_SetPlayer (String name)
    {
        this.name = name;
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return state.getPlayers().size() == 0;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        Player player = new Player(name);
        state.getPlayers().add(player);
    }

}
