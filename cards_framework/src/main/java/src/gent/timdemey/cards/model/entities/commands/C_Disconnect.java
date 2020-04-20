package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Let a player disconnect from the current online game.
 * 
 * @author Tim
 *
 */
public class C_Disconnect extends CommandBase
{
    C_Disconnect()
    {
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        return true;
    }

    @Override
    public void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);

        if (type == ContextType.UI)
        {
            state.getTcpConnectionPool().closeAllConnections();
            for (Player player : state.getRemotePlayers())
            {
                state.getPlayers().remove(player);
            }
            state.setServerId(null);
        }
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
