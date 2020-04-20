package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.payload.P_OnPlayerJoined;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

/**
 * Multicast sent to players in a lobby when a new player joined the lobby.
 * 
 * @author Tim
 *
 */
public class C_OnLobbyPlayerJoined extends CommandBase
{
    public final Player player;

    public C_OnLobbyPlayerJoined(Player player)
    {
        this.player = player;
    }

    public C_OnLobbyPlayerJoined(P_OnPlayerJoined pl)
    {
        super(pl);
        this.player = pl.player;
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
        state.getPlayers().add(player);
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("playerName", player.name) + Debug.getKeyValue("playerId", player.id);
    }
}
