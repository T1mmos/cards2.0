package gent.timdemey.cards.model.entities.cards.payload;

import java.util.List;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.common.PayloadBase;

public class P_CardStack extends PayloadBase
{
    public List<Card> cards;
    public String cardStackType;
    public int typeNumber;
}
