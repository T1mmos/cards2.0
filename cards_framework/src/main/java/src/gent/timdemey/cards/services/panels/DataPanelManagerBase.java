package gent.timdemey.cards.services.panels;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.contract.res.ImageResource;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.IScalableResource;
import gent.timdemey.cards.services.scaling.img.ScalableImageResource;
import gent.timdemey.cards.services.scaling.text.ScalableFontResource;

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
    public void onCreating(PanelInData<IN> inData)
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
    
    protected final void preloadImage(UUID resId, String path)
    {
        IResourceService resServ = Services.get(IResourceService.class);
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        ImageResource imgRes = resServ.getImage(path);
        IScalableResource<?> scaleRes_back = new ScalableImageResource(resId, imgRes);
        scaleCompServ.addScalableResource(scaleRes_back);
    }

    protected final void preloadFont(UUID resId, String path)
    {
        IResourceService resServ = Services.get(IResourceService.class);
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        FontResource resp_font = resServ.getFont(path);

        IScalableResource<?> scaleRes_font = new ScalableFontResource(resId, resp_font);
        scaleCompServ.addScalableResource(scaleRes_font);
    }
}