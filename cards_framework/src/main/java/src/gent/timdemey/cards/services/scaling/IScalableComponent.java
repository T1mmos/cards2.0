package gent.timdemey.cards.services.scaling;

import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.services.contract.Coords;

public interface IScalableComponent
{
    public void setMirror(boolean mirror);
    public boolean isMirror();
        
    public void setCoords(Coords coords);
    //public void setLocation(int x, int y);    
    
    public JComponent getComponent();
    public Coords getCoords();
    
    /**
     * The unique id of this component. 
     * @return
     */
    public UUID getId();
    
    /**
     * Updates the component to match the model state.
     */
    public void update();
    
    /**
     * Repaints the rendered component after some resources have changed behind its back. (e.g. when the resources have been rescaled)
     * This method should not be called after calling e.g. setForeground.
     */
    public void repaint();
}
