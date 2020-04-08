package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.payload.P_OnPlayerJoined;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_OnPlayerJoined extends CommandBase
{
    public final Player player;

    public C_OnPlayerJoined(Player player)
    {
        this.player = player;
    }

    public C_OnPlayerJoined(P_OnPlayerJoined pl)
    {
        super(pl);
        this.player = pl.player;
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    public void execute(Context context, ContextType contextType, State state)
    {
        CheckNotContext(contextType, ContextType.Server);
        if (contextType == ContextType.UI)
        {
            updateState(state);
        }
        else if (contextType == ContextType.Client)
        {
            updateState(state);
            reschedule(ContextType.UI);
        }
    }
    
    private void updateState(State state)
    {
        state.getPlayers().add(player);
    }
}
