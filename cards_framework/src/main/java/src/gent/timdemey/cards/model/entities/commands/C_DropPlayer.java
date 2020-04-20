package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

public class C_DropPlayer extends CommandBase
{

    private final UUID playerId;

    public C_DropPlayer(UUID playerId)
    {
        this.playerId = playerId;
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    public void execute(Context context, ContextType contextType, State state)
    {
        for (Player player : state.getPlayers())
        {
            if (player.id.equals(playerId))
            {
                state.getPlayers().remove(player);
            }
        }

        if (contextType == ContextType.Server)
        {
            String json = getCommandDtoMapper().toJson(this);
            for (Player player : state.getRemotePlayers())
            {
                state.getTcpConnectionPool().getConnection(player.id).send(json);
            }
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("playerId", playerId);
    }

}
