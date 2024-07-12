package gent.timdemey.cards.services.contract.descriptors;

public class PanelDescriptor extends DescriptorBase
{
    public final PanelType panelType;
    
    public PanelDescriptor(String id, PanelType panelType)
    {
        super(id);
        this.panelType = panelType;
    }
    
    @Override
    public final String toString()
    {
        return String.format("%s [id=%s, PanelType=%s]", getClass().getSimpleName(), id, panelType);
    }
}   
