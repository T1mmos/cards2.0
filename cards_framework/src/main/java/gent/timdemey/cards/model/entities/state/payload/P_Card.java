package gent.timdemey.cards.model.entities.state.payload;

import gent.timdemey.cards.model.entities.state.CardSuit;
import gent.timdemey.cards.model.entities.state.CardValue;
import gent.timdemey.cards.model.entities.common.PayloadBase;
import java.util.UUID;

public class P_Card extends PayloadBase
{
    public CardSuit suit;
    public CardValue value;
    public boolean visible;
    
    // cannot be included as also CardStack references a Card.
    // mappers will first create a CardStack with all its Card, and then
    // restore the domain model link Card->CardStack
    // public CardStack cardStack;
    
    public P_Card() {   }
    public P_Card(UUID id, CardSuit suit, CardValue value, boolean visible)
    {
        this.id = id;
        this.suit = suit;
        this.value = value;
        this.visible = visible;
    }
}
