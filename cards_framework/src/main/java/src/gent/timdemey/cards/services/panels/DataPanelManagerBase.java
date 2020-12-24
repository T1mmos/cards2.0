package gent.timdemey.cards.services.panels;

import java.util.EnumSet;

public abstract class DataPanelManagerBase<IN, OUT> extends PanelManagerBase implements IDataPanelManager<IN, OUT>
{
    protected static final EnumSet<PanelButtonType> SET_OK_CANCEL = EnumSet.of(PanelButtonType.Cancel, PanelButtonType.Ok);
    protected static final EnumSet<PanelButtonType> SET_CANCEL = EnumSet.of(PanelButtonType.Cancel);
    protected static final EnumSet<PanelButtonType> SET_OK = EnumSet.of(PanelButtonType.Ok);
    
    protected PanelInData<IN> inData;

    public DataPanelManagerBase()
    {
    }
    
    @Override
    public void load(PanelInData<IN> inData)
    {
        this.inData = inData;
    }
            
    protected final void verify(PanelButtonType dbType)
    {
        inData.verifyButtonFunc.accept(dbType);
    }
    
    protected final void close()
    {
        inData.closeFunc.run();
    }
    
    
    @Override
    public void preload()
    {
        
    }
    
}