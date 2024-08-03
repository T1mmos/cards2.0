package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_OnLobbyPlayerJoined;
import gent.timdemey.cards.model.entities.state.Player;
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

    public C_OnLobbyPlayerJoined(
        Container container,
        P_OnLobbyPlayerJoined parameters)
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
