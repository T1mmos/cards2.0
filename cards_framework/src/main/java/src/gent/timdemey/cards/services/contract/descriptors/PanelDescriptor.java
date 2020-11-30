package gent.timdemey.cards.services.contract.descriptors;

public class PanelDescriptor
{
    public final String id;
    public final int layer;
    public final boolean overlay;
    
    public PanelDescriptor(String id, int layer, boolean overlay)
    {
        this.id = id;
        this.layer = layer;
        this.overlay = overlay;
    }
    
    @Override
    public String toString()
    {
        return String.format("PanelDescriptor [id=%s, layer=%s, overlay=%s]", id, layer, overlay);
    }
}   
