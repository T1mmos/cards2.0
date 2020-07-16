package gent.timdemey.cards.services.contract.resdecr;

public final class ResourceUsageDescriptor
{
    public final String type;
    public final String subtype;
    
    public ResourceUsageDescriptor (String type)
    {
        this(type, null);
    }
    
    public ResourceUsageDescriptor (String type, String subtype)
    {
        this.type = type;
        this.subtype = subtype;
    }
}
