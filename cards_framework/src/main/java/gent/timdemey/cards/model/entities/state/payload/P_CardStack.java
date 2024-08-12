package gent.timdemey.cards.model.entities.state.payload;

import java.util.List;

import gent.timdemey.cards.model.entities.common.PayloadBase;
import gent.timdemey.cards.model.entities.state.Card;

public class P_CardStack extends PayloadBase
{
    public List<Card> cards;
    public String cardStackType;
    public int typeNumber;
}
