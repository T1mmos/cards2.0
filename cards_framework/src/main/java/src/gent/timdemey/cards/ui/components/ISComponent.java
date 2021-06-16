package gent.timdemey.cards.ui.components;

import java.util.UUID;

import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.ui.components.ext.IHasMouse;
import gent.timdemey.cards.ui.panels.IPanelManager;

public interface ISComponent
{        
    public ComponentType getComponentType();
    
    public IPanelManager getPanelManager();
    public void setPanelManager(IPanelManager panelManager);

    
    
    public void setCoords(Coords.Absolute coords);
    public Coords.Absolute getCoords();
    public void setLocation(int x, int y);    
    
    /**
     * The unique id of this component. 
     * @return
     */
    public UUID getId();
    
    
    public void add(IHasMouse listener);
    public void remove(IHasMouse listener);
}
