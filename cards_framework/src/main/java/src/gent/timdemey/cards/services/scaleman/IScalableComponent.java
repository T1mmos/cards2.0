package gent.timdemey.cards.services.scaleman;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.services.contract.Resource;

public interface IScalableComponent<R>
{
    public void setMirror(boolean mirror);
    public boolean isMirror();
        
    public void setBounds(Rectangle rect);
    public void setBounds(int x, int y, int w, int h);
    public void setLocation(int x, int y);    
    
    public JComponent getComponent();
    public Rectangle getBounds();
    
    /**
     * The unique id of this component. 
     * @return
     */
    public UUID getId();
        
    /**
     * Get a list of scalable resources needed by this IScalableComponent.
     * @return
     */
    public List<IScalableResource<R, Resource<R>>> getResources();
    
    /**
     * Updates the component to match the model state.
     */
    public void update();
    
    /**
     * Repaints the rendered component after some resources have changed behind its back. (e.g. when the resources have been rescaled)
     * This method should not be called after calling e.g. setForeground.
     */
    public void repaint();
    
    /**
     * Changes the foreground color of this component.
     * @param color
     */
    public void setForeground(Color color);
}
