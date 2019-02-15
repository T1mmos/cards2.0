package gent.timdemey.cards.entities;

public enum E_CardSuit {
    HEARTS ("♥"),
    DIAMONDS ("♦"),
    SPADES ("♠"),
    CLUBS ("♣"); 
    
    private final String textual;
    
    private E_CardSuit (String textual)
    {
        this.textual = textual;
    }
    
    public E_CardColor getColor()
    {
        return this == HEARTS || this == E_CardSuit.DIAMONDS ? E_CardColor.RED : E_CardColor.BLACK;
    }
    
    public String getTextual()
    {
        return textual;
    }
    
    public static E_CardSuit fromCharacter (String textual)
    {
        for (E_CardSuit cardType : E_CardSuit.values())
        {
            if (cardType.textual.equals(textual))
            {
                return cardType;
            }
        }
        
        throw new IllegalArgumentException("No such card type for text: " + textual);
    }
    
    @Override
    public String toString() {
        return getTextual();
    }
}
