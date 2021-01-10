package gent.timdemey.cards.services.contract.descriptors;

public class PanelDescriptor
{
    public final String id;
    public final PanelType panelType;
    
    public PanelDescriptor(String id, PanelType panelType)
    {
        this.id = id;
        this.panelType = panelType;
    }
    
    @Override
    public final String toString()
    {
        return String.format("%s [id=%s, PanelType=%s]", getClass().getSimpleName(), id, panelType);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
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
        PanelDescriptor other = (PanelDescriptor) obj;
        if(id == null)
        {
            if(other.id != null)
                return false;
        }
        else if(!id.equals(other.id))
            return false;
        return true;
    }
   
    
}   
