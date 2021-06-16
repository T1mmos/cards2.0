package gent.timdemey.cards.ui.panels;

import java.util.List;

import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.ui.components.ISComponent;
import gent.timdemey.cards.ui.components.JSLayeredPane;

public interface IPanelManager
{   
    public void preload();
    
    public boolean isPanelCreated();
    public JSLayeredPane createSPanel();
    public JSLayeredPane getSPanel();
    public void destroyPanel();    

    public void onShown();
    public void onHidden();
   // public boolean isVisible();
    
    public void createRescaleRequests(List<? super RescaleRequest> requests);
    
    public void createSComponents();
    public void positionSComponents();
    public void repaintSComponents();

    public void startAnimation(ISComponent scaleComp);
    public void stopAnimation(ISComponent scaleComp);

    public int getLayer(ISComponent scaleComp);
    public void setLayer(ISComponent scaleComp, int layerIndex);    
    
    public void add(ISComponent comp);
    public void remove(ISComponent comp);

    public void updateComponent(ISComponent comp);
}
