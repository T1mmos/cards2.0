package gent.timdemey.cards.ui.components.ext;

import java.util.UUID;

import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.ui.components.IAttachable;
import gent.timdemey.cards.ui.panels.IPanelManager;

public interface IComponent extends IAttachable
{        
    public UUID getId();
    public ComponentType getComponentType();
    
    public IPanelManager getPanelManager();
    public void setPanelManager(IPanelManager panelManager);    
    
    public void setAbsCoords(Coords.Absolute coords);
    public Coords.Absolute getAbsCoords();
    public void setLocation(int x, int y);    

    Object getPayload();    
    void setPayload(Object payload);
}
