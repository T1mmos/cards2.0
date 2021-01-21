package gent.timdemey.cards.services.contract.descriptors;

public abstract class DescriptorBase
{
    public final String id;

    protected DescriptorBase (String id)
    {
        if (id == null || id.isEmpty())
        {
            throw new NullPointerException("id");
        }
        
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
        if (this == obj)
        {
            return true;
        }            
        if (obj == null)
        {
            return false;
        }            
        if (getClass() != obj.getClass())
        {
            return false;
        }
            
        DescriptorBase other = (DescriptorBase) obj;
        return this.id.equals(other.id);
    }
    
    @Override
    public String toString()
    {
        return String.format(getClass().getSimpleName() + " { id=%s }", id);  
    }
}
