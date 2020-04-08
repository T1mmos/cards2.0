package gent.timdemey.cards.model.entities.cards.payload;

import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_Card extends PayloadBase
{
    public Suit suit;
    public Value value;
    public boolean visible;
    public CardStack cardStack;
}
