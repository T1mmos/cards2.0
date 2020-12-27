package gent.timdemey.cards.services.contract.descriptors;

public class DataPanelDescriptor<IN, OUT> extends PanelDescriptor
{    
    public DataPanelDescriptor(String id, int layer)
    {
        super(id, layer);
    }
    
    @Override
    public String toString()
    {
        return String.format("DataPanelDescriptor [id=%s, layer=%s]", id, layer);
    }
}   
