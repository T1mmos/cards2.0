package gent.timdemey.cards.ui.panels;

import java.util.List;

import javax.swing.JComponent;

import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;

public interface IPanelManager
{   
    public void preload();
    
    public boolean isPanelCreated();
    public JSLayeredPane createPanel();
    public JSLayeredPane getPanel();
    public void destroyPanel();    

    public void onShown();
    public void onHidden();
   // public boolean isVisible();
    
    public void createRescaleRequests(List<? super RescaleRequest> requests);
    
    public void createComponents();
    public void positionComponents();

    public void startAnimation(IComponent scaleComp);
    public void stopAnimation(IComponent scaleComp);
}
