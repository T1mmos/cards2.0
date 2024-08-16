package gent.timdemey.cards.model.entities.state;

import static gent.timdemey.cards.model.entities.state.CardOrder.AceToKing;
import static gent.timdemey.cards.model.entities.state.CardOrder.TwoToAce;

public enum CardValue
{
    V_2("2", 0), V_3("3", 1), V_4("4", 2), V_5("5", 3), V_6("6", 4), V_7("7", 5), V_8("8", 6), V_9("9", 7),
    V_10("10", 8), V_J("J", 9), V_Q("Q", 10), V_K("K", 11), V_A("A", 12);

    private final String textual;
    private final int index;

    private CardValue(String textual, int order)
    {
        this.textual = textual;
        this.index = order;
    }

    public String getTextual()
    {
        return textual;
    }

    public static CardValue fromCharacter(String textual)
    {
        for (CardValue cardValue : CardValue.values())
        {
            if (cardValue.textual.equals(textual))
            {
                return cardValue;
            }
        }

        throw new IllegalArgumentException("No such card value for text: " + textual);
    }
        
    public int getIndex(CardOrder cardOrder)
    {
        return getIndex(cardOrder, index);
    }
    
    public static CardValue getCardValue(CardOrder cardOrder, int order)
    {
        int order_atok = getIndex(cardOrder, order);
        return getAKCardValue(order_atok);
    }
    
    private static int getIndex(CardOrder cardOrder, int order)
    {
        switch (cardOrder)
        {
            case TwoToAce:
                return order;
            case AceToKing:
                return (order + 1) % 13;
            default:
                throw new IllegalArgumentException("No such CardOrder supported: " + cardOrder);
        }
    }
    
    private static CardValue getAKCardValue(int order)
    {
        for (CardValue cardValue : CardValue.values())
        {
            if (cardValue.index == order)
            {
                return cardValue;
            }
        }
        
        throw new IllegalArgumentException("No such card value for order: " + order);
    }

    @Override
    public String toString()
    {
        return getTextual();
    }
}
