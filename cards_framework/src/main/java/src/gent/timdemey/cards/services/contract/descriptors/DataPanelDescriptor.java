package gent.timdemey.cards.services.contract.descriptors;

public class DataPanelDescriptor<IN, OUT> extends PanelDescriptor
{
    
    public DataPanelDescriptor(String id, int layer, boolean overlay)
    {
        super(id, layer, overlay);
    }
    
    @Override
    public String toString()
    {
        return String.format("DataPanelDescriptor [id=%s, layer=%s, overlay=%s]", id, layer, overlay);
    }
}   
