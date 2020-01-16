package gent.timdemey.cards.model.cards;

public enum Suit
{
    HEARTS("♥"), DIAMONDS("♦"), SPADES("♠"), CLUBS("♣");

    private final String textual;

    private Suit(String textual)
    {
        this.textual = textual;
    }

    public SuitColor getColor()
    {
        return this == HEARTS || this == Suit.DIAMONDS ? SuitColor.RED : SuitColor.BLACK;
    }

    public String getTextual()
    {
        return textual;
    }

    public static Suit fromCharacter(String textual)
    {
        for (Suit cardType : Suit.values())
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
