package gent.timdemey.cards.readonlymodel;

import java.util.UUID;

import gent.timdemey.cards.model.commands.CommandHistory;
import gent.timdemey.cards.model.state.State;

public class ReadOnlyState extends ReadOnlyEntityBase<State>
{
    public ReadOnlyState(State state)
    {
        super(state);
    }
    
    public ReadOnlyCardGame getCardGame()
    {
        return ReadOnlyEntityFactory.getOrCreateCardGame(entity.getCardGame());
    }
    
    public ReadOnlyEntityList<ReadOnlyPlayer> getPlayers()
    {
        return ReadOnlyEntityFactory.getOrCreatePlayerList(entity.getPlayers());
    }
    
    public CommandHistory getCommandHistory()
    {
        return entity.getCommandHistory();
    }
    
    public ReadOnlyEntityList<ReadOnlyServer> getServers()
    {
        return ReadOnlyEntityFactory.getOrCreateServerList(entity.getServers());
    }
    
    public UUID getLocalId()
    {
        return entity.getLocalId();
    }
}
