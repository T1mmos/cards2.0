package gent.timdemey.cards.services.contract.descriptors;

import java.util.function.Function;

import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor.ResourceDescriptorP1;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor.ResourceDescriptorP2;

public class ResourceDescriptors
{ 
    public static final ResourceDescriptor                     AppBackground =          get("AppBackground"); 
    public static final ResourceDescriptorP2<Integer, Integer> AppIcon =                get("AppIcon", ResourceFunctions.IntFunc, ResourceFunctions.IntFunc); 
    public static final ResourceDescriptor                     CardBack =               get("CardBack");
    public static final ResourceDescriptorP2<Suit, Value>      CardFront =              get("CardFront", ResourceFunctions.SuitFunc, ResourceFunctions.ValueFunc);
                                                                                        
    public static final ResourceDescriptor                     FontMenu =               get("FontMenu");
    public static final ResourceDescriptor                     AppTitleFont =           get("AppTitleFont");
    public static final ResourceDescriptor                     AppMinimize =            get("AppMinimize");
    public static final ResourceDescriptor                     AppMinimizeRollover =    get("AppMinimizeRollover");
    public static final ResourceDescriptor                     AppMaximize=             get("AppMaximize");
    public static final ResourceDescriptor                     AppMaximizeRollover =    get("AppMaximizeRollover");
    public static final ResourceDescriptor                     AppUnmaximize =          get("AppUnmaximize");
    public static final ResourceDescriptor                     AppUnmaximizeRollover =  get("AppUnmaximizeRollover");
    public static final ResourceDescriptor                     AppClose =               get("AppClose");
    public static final ResourceDescriptor                     AppCloseRollover =       get("AppCloseRollover");
    public static final ResourceDescriptor                     DialogBackground =       get("DialogBackground");
    public static final ResourceDescriptor                     DialogTitleFont =        get("DialogTitleFont");
    
    
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
    
    private ResourceDescriptors()
    {
    }
}
