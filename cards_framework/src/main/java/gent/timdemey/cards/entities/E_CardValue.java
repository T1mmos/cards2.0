package gent.timdemey.cards.entities;

public enum E_CardValue {
    
    V_2 ("2", 0),
    V_3 ("3", 1),
    V_4 ("4", 2),
    V_5 ("5", 3),
    V_6 ("6", 4),
    V_7 ("7", 5),
    V_8 ("8", 6),
    V_9 ("9", 7),
    V_10 ("10", 8),
    V_J ("J", 9),
    V_Q ("Q", 10),
    V_K ("K", 11),
    V_A ("A", 12);
    
    private final String textual;
    private final int order;
    
    private E_CardValue(String textual, int order)
    {
        this.textual = textual;
        this.order = order;
    }
    
    public String getTextual()
    {
        return textual;
    }
    
    public int getOrder2toA()
    {
        return order;
    }
    
    public int getOrderAtoK()
    {
        return (order +  1) % 13;
    }
    
    public static E_CardValue fromCharacter (String textual)
    {
        for (E_CardValue cardValue : E_CardValue.values())
        {
            if (cardValue.textual.equals(textual))
            {
                return cardValue;
            }
        }
        
        throw new IllegalArgumentException("No such card value for text: " + textual);
    }    
    
    public static E_CardValue fromOrder2toA (int order)
    {
        for (E_CardValue cardValue : E_CardValue.values())
        {
            if (cardValue.order == order)
            {
                return cardValue;
            }
        }
        
        throw new IllegalArgumentException("No such card value for order: " + order);
    }
    
    public static E_CardValue fromOrderAtoK (int order)
    {
        return fromOrder2toA((order + 13 - 1) % 13);
    }
        
    @Override
    public String toString() {
        return getTextual();
    }
}
