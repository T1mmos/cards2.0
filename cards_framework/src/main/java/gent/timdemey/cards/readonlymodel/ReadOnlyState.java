package gent.timdemey.cards.readonlymodel;

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
    
    public ReadOnlyList<ReadOnlyPlayer> getPlayers()
    {
        return ReadOnlyEntityFactory.getOrCreatePlayerList(entity.getPlayers());
    }
}
