package gent.timdemey.cards.model.entities.state;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.utils.Debug;

public class PlayerConfiguration extends EntityBase
{
    public final UUID playerId;
    public final List<CardStack> cardStacks;
        
    PlayerConfiguration(UUID id, UUID playerId, List<CardStack> cardStacks)
    {
        super(id);
        this.playerId = playerId;
        this.cardStacks = cardStacks;
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("playerId", playerId);               
    }
}
