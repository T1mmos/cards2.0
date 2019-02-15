package gent.timdemey.cards.entities;

import java.util.UUID;

public final class E_Card {
        
    public static class CompactConverter extends ASerializer<E_Card>
    {
        @Override
        protected void write(SerializationContext<E_Card> sc) {
            writeUUID(sc, PROPERTY_CARD_ID, sc.src.cardId);
            
            String valtex = sc.src.cardValue.getTextual();
            String typetex = sc.src.cardSuit.getTextual();
            writeString(sc, PROPERTY_CARD, valtex + typetex);
            
            writeBoolean(sc, PROPERTY_CARD_VISIBLE, sc.src.visible);
        }

        @Override
        protected E_Card read(DeserializationContext dc) {
            UUID cardId = readUUID(dc, PROPERTY_CARD_ID);
            
            String toParse = readString(dc, PROPERTY_CARD);
            int exc10 = (toParse.length() == 3 ? 1 : 0);
            String valtex = toParse.substring(0, 1 + exc10);
            String typetex = toParse.substring(1 + exc10, 2 + exc10);            
            E_CardValue value = E_CardValue.fromCharacter(valtex);
            E_CardSuit suit = E_CardSuit.fromCharacter(typetex);
            
            boolean visible = readBoolean(dc, PROPERTY_CARD_VISIBLE);
            
            return new E_Card(cardId, suit, value, visible);
        }        
    }
            
    private UUID cardId;
    private final E_CardSuit cardSuit;
    private final E_CardValue cardValue;
    
    private boolean visible;
    private E_CardStack cardStack;
    
    private E_Card (UUID cardId, E_CardSuit cardSuit, E_CardValue cardValue, boolean visible)
    {
        this.cardId = cardId;
        this.cardSuit = cardSuit;
        this.cardValue = cardValue;
        this.visible = visible;
    }
    
    E_Card (E_CardSuit cardSuit, E_CardValue cardValue, boolean visible)
    {
        this(UUID.randomUUID(), cardSuit, cardValue, visible);
    }
        
    public E_CardSuit getCardSuit()
    {
        return cardSuit;
    }
    
    public E_CardValue getCardValue()
    {
        return cardValue;
    }
    
    public E_CardColor getCardColor ()
    {
        return cardSuit.getColor();
    }
    
    public boolean isVisible()
    {
        return visible;
    }
    
    public E_CardStack getCardStack()
    {
        return this.cardStack;
    }
    
    public int getCardIndex()
    {
        return this.cardStack.indexOf(this);
    }    
    
    public UUID getCardId()
    {
        return cardId;
    }
    
    void setVisible(boolean visible)
    {
        this.visible = visible;
    }
    
    void setCardStack(E_CardStack cardStack)
    {
        this.cardStack = cardStack;
    }    
    
    public String getTextual()
    {
        return "" + cardValue + cardSuit;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof E_Card)){
            return false;
        }
        
        E_Card other = (E_Card) obj;
        return cardId.equals(other.cardId);
    }
     
    @Override
    public int hashCode() {
        return cardId.hashCode();
    }
    
    @Override
    public String toString() {
        return getTextual();
    }
}
