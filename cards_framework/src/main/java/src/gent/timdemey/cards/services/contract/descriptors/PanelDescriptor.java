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
}   
