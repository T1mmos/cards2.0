package gent.timdemey.cards.services.panels;

import java.util.List;

import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public interface IPanelManager
{   
    public void preload();
    
    public boolean isCreated();
    public PanelBase create();
    public PanelBase get();
    public void destroy();    

    public void setVisible(boolean b);
    public boolean isVisible();
    
    public void createRescaleRequests(List<? super RescaleRequest> requests);
    
    public void createScalableComponents();
    public void positionScalableComponents();
    public void repaintScalableComponents();

    public void startAnimation(IScalableComponent scaleComp);
    public void stopAnimation(IScalableComponent scaleComp);

    public int getLayer(IScalableComponent scaleComp);
    public void setLayer(IScalableComponent scaleComp, int layerIndex);    
    
    public void add(IScalableComponent comp);
    public void remove(IScalableComponent comp);

    public void updateComponent(IScalableComponent comp);



}
