package gent.timdemey.cards.model.entities.state;

import java.util.UUID;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.common.EntityList;
import gent.timdemey.cards.model.entities.state.payload.P_PlayerConfiguration;
import gent.timdemey.cards.utils.Debug;

public class PlayerConfiguration extends EntityBase
{
    public final UUID playerId;
    public final EntityList<CardStack> cardStacks;
        
    public PlayerConfiguration(
        P_PlayerConfiguration parameters)
    {
        super(parameters);
        this.playerId = parameters.playerId;        
        this.cardStacks = EntityList.from(parameters.cardStacks);
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("playerId", playerId);               
    }
}
