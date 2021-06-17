package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;

public interface IAnimationService
{
    /**
     * Returns an object that describes the animation a scalable component
     * should go through.
     * @param comp
     * @return
     */
    public void animate (JSLayeredPane pb);
    
    public void animate (IComponent comp);
}
