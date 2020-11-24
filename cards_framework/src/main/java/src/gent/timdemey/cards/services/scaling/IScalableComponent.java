package gent.timdemey.cards.services.scaling;

import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;

public interface IScalableComponent
{    
    public JComponent getComponent();
        
    public void setMirror(boolean mirror);
    public boolean isMirror();
        
    public ComponentType getComponentType();
    
    /**
     * Set an arbitrary object as a payload of this component.
     * @param payload
     */
    public void setPayload(Object payload);
    public Object getPayload();
    
    public void setCoords(Coords.Absolute coords);
    public Coords.Absolute getCoords();
    public void setLocation(int x, int y);    
    
    /**
     * The unique id of this component. 
     * @return
     */
    public UUID getId();
    
    /**
     * Repaints the rendered component after some resources have changed behind its back. (e.g. when the resources have been rescaled)
     * This method should not be called after calling e.g. setForeground.
     */
    public void repaint();
}
