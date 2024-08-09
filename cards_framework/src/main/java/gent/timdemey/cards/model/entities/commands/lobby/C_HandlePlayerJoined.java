package gent.timdemey.cards.model.entities.commands.lobby;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.Player;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

/**
 * Multicast sent to players in a lobby when a new player joined the lobby.
 * 
 * @author Tim
 *
 */
public class C_HandlePlayerJoined extends CommandBase<P_HandlePlayerJoined>
{
    public final Player player;

    public C_HandlePlayerJoined(
        Container container,
        P_HandlePlayerJoined parameters)
    {
        super(container, parameters);
        
        this.player = parameters.player;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        CheckContext(ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        CheckContext(ContextType.UI);
        _State.getPlayers().add(player);
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("playerName", player.getName()) + Debug.getKeyValue("playerId", player.id);
    }
}
