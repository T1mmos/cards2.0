package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

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
        if (type != ContextType.UI)
        {
            throw new IllegalStateException("This is a single player command which can only execute in the UI layer");
        }
        
        Player player = new Player(name);
        state.getPlayers().add(player);
        state.setLocalId(player.id);
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("name", name);
    }

}
