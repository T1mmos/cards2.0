package gent.timdemey.cards.services.contract.descriptors;

import java.util.function.Function;

import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.services.contract.ButtonState;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor.ResourceDescriptorP1;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor.ResourceDescriptorP2;

public class ResourceDescriptors
{ 
    public static final Function<Suit, String>                  SuitFunc =              suit -> suit.name().substring(0, 1); 
    public static final Function<Value, String>                 ValueFunc =             value -> value.getTextual();
    public static final Function<Integer, String>               IntFunc =               i -> "" + i;
    public static final Function<ButtonState, String>           ButtonStateFunc =       ResourceDescriptors::fromButtonState;
    
    public static final ResourceDescriptor                      AppBackground =         get("AppBackground"); 
    public static final ResourceDescriptorP2<Integer, Integer>  AppIcon =               get("AppIcon", IntFunc, IntFunc); 
    public static final ResourceDescriptor                      CardBack =              get("CardBack");
    public static final ResourceDescriptorP2<Suit, Value>       CardFront =             get("CardFront", SuitFunc, ValueFunc);
                                                                                        
    public static final ResourceDescriptor                      FontMenu =              get("FontMenu");
    public static final ResourceDescriptorP1<ButtonState>       AppClose =              get("AppClose", ButtonStateFunc);
    public static final ResourceDescriptorP1<ButtonState>       AppMinimize =           get("AppMinimize", ButtonStateFunc);
    public static final ResourceDescriptorP1<ButtonState>       AppMaximize=            get("AppMaximize", ButtonStateFunc);
    public static final ResourceDescriptor                      AppTitleFont =          get("AppTitleFont");
    public static final ResourceDescriptorP1<ButtonState>       AppUnmaximize =         get("AppUnmaximize", ButtonStateFunc);
    public static final ResourceDescriptor                      DialogBackground =      get("DialogBackground");
    public static final ResourceDescriptor                      DialogTitleFont =       get("DialogTitleFont");
    
    public static final ResourceDescriptor get(String id)
    {
        return new ResourceDescriptor(id);
    }
    
    public static final <P1> ResourceDescriptorP1<P1> get(String id, Function<P1, String> func1)
    {
        return new ResourceDescriptorP1<P1>(id, func1);
    }
    
    public static final <P1, P2> ResourceDescriptorP2<P1, P2> get(String id, Function<P1, String> func1, Function<P2, String> func2)
    {
        return new ResourceDescriptorP2<P1, P2>(id, func1, func2);
    }
    
    private static String fromButtonState(ButtonState buttonState) 
    {
        if (buttonState == ButtonState.Normal)
        {
            return "";
        }
        else 
        {
            return "_" + buttonState.name().toLowerCase();
        }
    }
    
    private ResourceDescriptors()
    {
    }
}
