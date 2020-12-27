package gent.timdemey.cards.services.contract.descriptors;

public class PanelDescriptor
{
    public final String id;
    public final int layer;
    
    public PanelDescriptor(String id, int layer)
    {
        this.id = id;
        this.layer = layer;
    }
    
    @Override
    public String toString()
    {
        return String.format("PanelDescriptor [id=%s, layer=%s]", id, layer);
    }
}   
