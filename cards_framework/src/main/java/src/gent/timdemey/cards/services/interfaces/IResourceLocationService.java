package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor.ResourceDescriptorP1;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptor.ResourceDescriptorP2;

public interface IResourceLocationService
{
    public String getFilePath(ResourceDescriptor imgDesc);
    public <P1> String getFilePath(ResourceDescriptorP1<P1> imgDesc, P1 param1);
    public <P1, P2> String getFilePath(ResourceDescriptorP2<P1, P2> imgDesc, P1 param1 , P2 param2);
}
