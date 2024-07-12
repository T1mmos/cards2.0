package gent.timdemey.cards.model.entities.cards.payload;

import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_Card extends PayloadBase
{
    public Suit suit;
    public Value value;
    public boolean visible;
    
    // cannot be included as also CardStack references a Card.
    // mappers will first create a CardStack with all its Card, and then
    // restore the domain model link Card->CardStack
    // public CardStack cardStack;
}
