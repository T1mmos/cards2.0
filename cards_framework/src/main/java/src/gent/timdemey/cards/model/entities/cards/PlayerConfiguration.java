package gent.timdemey.cards.model.entities.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.payload.P_PlayerConfiguration;
import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.utils.Debug;

public class PlayerConfiguration extends EntityBase
{
    public final UUID playerId;
    public final List<CardStack> cardStacks;
    
    public PlayerConfiguration (UUID playerId,  List<CardStack> cardStacks)
    {
        this.playerId = playerId;
        this.cardStacks = Collections.unmodifiableList(new ArrayList<>(cardStacks));
    }
    
    public PlayerConfiguration(P_PlayerConfiguration pl)
    {
        super(pl);
        this.playerId = pl.playerId;
        this.cardStacks = pl.cardStacks;
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("playerId", playerId);               
    }
}
