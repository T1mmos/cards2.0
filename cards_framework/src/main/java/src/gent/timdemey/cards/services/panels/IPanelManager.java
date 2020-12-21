package gent.timdemey.cards.services.panels;

import java.util.List;

import javax.swing.JComponent;

import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public interface IPanelManager
{
    public void preload();
   
    public boolean isCreated();
    public JComponent getOrCreate();
    public void onShown(); 
    public void onHidden();
    public void destroy();
    
    public void createRescaleRequests(List<? super RescaleRequest> requests);
    public void createScalableComponents();
    public void positionScalableComponents();
    void relayout();
    public void onResourcesRescaled();    

    public void startAnimation(IScalableComponent scaleComp);
    public void stopAnimation(IScalableComponent scaleComp);

    public int getLayer(IScalableComponent scaleComp);
    public void setLayer(IScalableComponent scaleComp, int layerIndex);    
    
 /*   public void add(IScalableComponent comp);
    public void remove(IScalableComponent comp);
*/
    public void updateComponent(IScalableComponent comp);

}
