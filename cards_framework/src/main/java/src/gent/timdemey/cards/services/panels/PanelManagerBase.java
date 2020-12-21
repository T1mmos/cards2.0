package gent.timdemey.cards.services.panels;

import java.util.List;

import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public abstract class PanelManagerBase implements IPanelManager
{
    @Override
    public void preload()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onShown()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void createRescaleRequests(List<? super RescaleRequest> requests)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void createScalableComponents()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void positionScalableComponents()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void relayout()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onResourcesRescaled()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startAnimation(IScalableComponent scaleComp)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stopAnimation(IScalableComponent scaleComp)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getLayer(IScalableComponent scaleComp)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setLayer(IScalableComponent scaleComp, int layerIndex)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public void updateComponent(IScalableComponent comp)
    {
        // TODO Auto-generated method stub
        
    }

}
