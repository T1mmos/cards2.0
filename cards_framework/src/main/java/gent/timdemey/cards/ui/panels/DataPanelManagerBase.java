package gent.timdemey.cards.ui.panels;

import gent.timdemey.cards.di.Container;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptors;

public abstract class DataPanelManagerBase<IN, OUT> extends PanelManagerBase implements IDataPanelManager<IN, OUT>
{
    protected static final List<PanelButtonDescriptor> SET_OK_CANCEL = buttonlist(PanelButtonDescriptors.Cancel, PanelButtonDescriptors.Ok);
    protected static final List<PanelButtonDescriptor> SET_CANCEL = buttonlist(PanelButtonDescriptors.Cancel);
    protected static final List<PanelButtonDescriptor> SET_OK = buttonlist(PanelButtonDescriptors.Ok);
    
    protected static List<PanelButtonDescriptor> buttonlist (PanelButtonDescriptor ... buts)
    {
        return Collections.unmodifiableList(Arrays.asList(buts));
    }
    
    protected PanelInData<IN> inData;

    public DataPanelManagerBase(Container container)
    {
        super(container);
    }
    
    @Override
    public void load(PanelInData<IN> inData)
    {
        this.inData = inData;
    }
            
    protected final void verify(PanelButtonDescriptor dbType)
    {
        inData.verifyButtonFunc.accept(dbType);
    }
    
    @Override
    public void preload()
    {
        
    }
    
}