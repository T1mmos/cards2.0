package gent.timdemey.cards.services.resources;

import java.util.HashMap;
import java.util.Map;

import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor.ResourceDescriptorP1;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor.ResourceDescriptorP2;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.interfaces.IResourceNameService;

public class ResourceNameService implements IResourceNameService
{    
    private static Map<ResourceDescriptor, String> RESOURCES0 = new HashMap<>();
    private static Map<ResourceDescriptorP1<?>, String> RESOURCES1 = new HashMap<>();
    private static Map<ResourceDescriptorP2<?,?>, String> RESOURCES2 = new HashMap<>();
    
    static
    {
        RESOURCES0.put(ResourceDescriptors.CardBack,            "cards/edge_thick/backside_yellow.png"  );
        RESOURCES0.put(ResourceDescriptors.FontMenu,            "ARCADE.TTF"                            );
        RESOURCES0.put(ResourceDescriptors.AppTitleFont,        "foptitles.ttf"                         );
        RESOURCES0.put(ResourceDescriptors.AppBackground,       "background_softblue.png"               );
        RESOURCES0.put(ResourceDescriptors.DialogBackground,    "bg_olive.png"                          );
        RESOURCES0.put(ResourceDescriptors.DialogTitleFont,     "SMB2.ttf"                              );
        RESOURCES0.put(ResourceDescriptors.Menu,                "cards-A-50.png"                        );
        
        RESOURCES0.put(ResourceDescriptors.SoundTest,           "test.wav"                              );
        RESOURCES0.put(ResourceDescriptors.SoundPutDown,        "putdown.wav"                           );
        
        RESOURCES1.put(ResourceDescriptors.AppClose,            "close%s.png"                           );
        RESOURCES1.put(ResourceDescriptors.AppMaximize,         "maximize%s.png"                        );
        RESOURCES1.put(ResourceDescriptors.AppMinimize,         "minimize%s.png"                        );
        RESOURCES1.put(ResourceDescriptors.AppUnmaximize,       "unmaximize%s.png"                      );        

        RESOURCES2.put(ResourceDescriptors.AppIcon,             "icon_spade_%sx%s.png"                  );
        RESOURCES2.put(ResourceDescriptors.CardFront,           "cards/edge_thick/%s_%s.png"            );
    }
        
    @Override
    public String getFilePath(ResourceDescriptor resDesc)
    {
        if (resDesc == null)
        {
            throw new NullPointerException("imgDesc");
        }
        
        String raw = getRawFilePath(RESOURCES0, resDesc);
        return raw;
    }

    @Override
    public <P1> String getFilePath(ResourceDescriptorP1<P1> resDesc, P1 param1)
    {
        if (resDesc == null)
        {
            throw new NullPointerException("imgDesc");
        }
        if (param1 == null)
        {
            throw new NullPointerException("param1");
        }
        
        String raw = getRawFilePath(RESOURCES1, resDesc);
        String filepath = resDesc.get(raw, param1);
        return filepath;
    }

    @Override
    public <P1, P2> String getFilePath(ResourceDescriptorP2<P1, P2> resDesc, P1 param1, P2 param2)
    {
        if (resDesc == null)
        {
            throw new NullPointerException("imgDesc");
        }
        if (param1 == null)
        {
            throw new NullPointerException("param1");
        }
        if (param2 == null)
        {
            throw new NullPointerException("param2");
        }
        
        String raw = getRawFilePath(RESOURCES2, resDesc);
        String filepath = resDesc.get(raw, param1, param2);
        return filepath;
    }
    
    private <T> String getRawFilePath (Map<T, String> map, T key)
    {
        String filename = map.get(key);
        if (filename == null)
        {
            throw new IllegalArgumentException("The given ResourceDescriptor cannot be mapped onto a filename: " + key);
        }
        return filename;
    }
}
