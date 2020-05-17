package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_OnLobbyPlayerJoined;
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

    public C_OnLobbyPlayerJoined(P_OnLobbyPlayerJoined pl)
    {
        super(pl);
        this.player = pl.player;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    public void preExecute(Context context, ContextType type, State state)
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
