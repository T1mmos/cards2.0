package gent.timdemey.cards.services.contract.descriptors;

public final class ComponentDescriptor
{
    public final String type;
    public final String subtype;
    
    public ComponentDescriptor (String type)
    {
        this(type, null);
    }
    
    public ComponentDescriptor (String type, String subtype)
    {
        this.type = type;
        this.subtype = subtype;
    }
    
    @Override
    public String toString()
    {
        return String.format("type=%s,subType=%s", type, subtype); 
    }
}
