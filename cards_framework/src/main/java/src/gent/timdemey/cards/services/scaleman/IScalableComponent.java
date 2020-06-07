package gent.timdemey.cards.services.scaleman;

import java.awt.Rectangle;
import java.util.List;

import javax.swing.JComponent;

public interface IScalableComponent
{
    public void setMirror(boolean mirror);
    public boolean getMirror();
        
    public void setBounds(Rectangle rect);
    public void setBounds(int x, int y, int w, int h);
    public void setLocation(int x, int y);
    
    public JComponent getComponent();
    public Rectangle getBounds();
    
    /**
     * The unique id of this component. 
     * UUID id, 
     * @return
     */
    public String getId();
    
    /**
     * Get a list of scalable resources needed by this IScalableComponent.
     * @return
     */
    public List<? extends IScalableResource> getResources();
    
    /**
     * Update the rendered component. (e.g. when the resources have been rescaled)
     */
    public void update();
    
}
