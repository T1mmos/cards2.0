package gent.timdemey.cards.services.contract.descriptors;

public class ActionDescriptor
{
    public final String id;
    
    ActionDescriptor(String id)
    {
        this.id = id;
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        ActionDescriptor other = (ActionDescriptor) obj;
        return this.id.equals(other.id);
    }
    
    @Override
    public String toString()
    {
        return String.format("ActionDescriptor { id=%s }", id);  
    }
}
