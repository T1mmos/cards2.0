package gent.timdemey.cards.services.resources;

import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor.ResourceDescriptorP1;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor.ResourceDescriptorP2;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;

public class ResourceLocationService implements IResourceLocationService
{    
    @Override
    public String getFilePath(ResourceDescriptor resDesc)
    {
        if (resDesc == null)
        {
            throw new NullPointerException("imgDesc");
        }
        
        if (resDesc == ResourceDescriptors.CardBack)
        {
            return "cards/edge_thick/backside_yellow.png";
        }
        else if (resDesc == ResourceDescriptors.FontMenu)
        {
            return "ARCADE.TTF";            
        }
        else if (resDesc == ResourceDescriptors.AppTitleFont)
        {
            return "foptitles.ttf";
        }
        else if (resDesc == ResourceDescriptors.AppBackground)
        {
            return "background_softblue.png";
        }
        else if (resDesc == ResourceDescriptors.DialogBackground)
        {
            return "bg_olive.png";
        }
        else if (resDesc == ResourceDescriptors.DialogTitleFont)
        {
            return "SMB2.ttf";
        }
        
        throw new IllegalArgumentException("Unknown ResourceDescriptor type: " + resDesc.getClass().getSimpleName());
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
        
        String path = null;
        if (resDesc == ResourceDescriptors.AppClose)
        {
            path = "close%s.png";
        }
        else if (resDesc == ResourceDescriptors.AppMaximize)
        {
            path = "maximize%s.png";
        }
        else if (resDesc == ResourceDescriptors.AppMinimize)
        {
            path = "minimize%s.png";
        }
        else if (resDesc == ResourceDescriptors.AppUnmaximize)
        {
            path = "unmaximize%s.png";
        }
        
        if (path != null)
        {
            return resDesc.get(path, param1);
        }
        
        throw new IllegalArgumentException("Unknown ResourceDescriptor: " + resDesc);
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
        
        String path = null;
        if (resDesc == ResourceDescriptors.AppIcon)
        {
            path = "icon_spade_%sx%s.png";
        }
        else if (resDesc == ResourceDescriptors.CardFront)
        {
            path = "cards/edge_thick/%s_%s.png";
        }        
        
        if (path != null)
        {
            return resDesc.get(path, param1, param2);
        }
        
        throw new IllegalArgumentException("Unknown ResourceDescriptor: " + resDesc);
    }
}
