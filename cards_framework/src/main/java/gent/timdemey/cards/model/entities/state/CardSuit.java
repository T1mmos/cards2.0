package gent.timdemey.cards.model.entities.state;

public enum CardSuit
{
    HEARTS("♥"), DIAMONDS("♦"), SPADES("♠"), CLUBS("♣");

    private final String textual;

    private CardSuit(String textual)
    {
        this.textual = textual;
    }

    public SuitColor getColor()
    {
        return this == HEARTS || this == CardSuit.DIAMONDS ? SuitColor.RED : SuitColor.BLACK;
    }

    public String getTextual()
    {
        return textual;
    }

    public static CardSuit fromCharacter(String textual)
    {
        for (CardSuit cardType : CardSuit.values())
        {
            if (cardType.textual.equals(textual))
            {
                return cardType;
            }
        }

        throw new IllegalArgumentException("No such card type for text: " + textual);
    }

    @Override
    public String toString()
    {
        return getTextual();
    }
}
