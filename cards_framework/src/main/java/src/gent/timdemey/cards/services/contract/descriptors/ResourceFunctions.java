package gent.timdemey.cards.services.contract.descriptors;

import java.util.function.Function;

import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;

public class ResourceFunctions
{
    public static final Function<Suit, String> SuitFunc = suit -> suit.name().substring(0, 1); 
    public static final Function<Value, String> ValueFunc = value -> value.getTextual();
    public static final Function<Integer, String> IntFunc = i -> "" + i;
    
    private ResourceFunctions() 
    {        
    }
}
