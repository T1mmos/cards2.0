package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.model.State;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_HandlePlayerJoined extends CommandBase
{    
    final ReadOnlyPlayer player;
    
    C_HandlePlayerJoined(ReadOnlyPlayer player) 
    {
        this.player = player;
    }

    @Override
    public void execute(Context context, ContextType contextType, State state) 
    {
        if (contextType == ContextType.UI)
        {
            context.addPlayer(player.id, player.name);
        } 
        else if  (contextType == ContextType.Client)
        {
            context.addPlayer(player.id, player.name);
            reschedule(ContextType.UI);
        }
        else 
        {
            throw new IllegalStateException();
        }
    }
}
