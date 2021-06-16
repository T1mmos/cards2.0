package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.ui.components.ISComponent;
import gent.timdemey.cards.ui.components.JSLayeredPane;

public interface IAnimationService
{
    /**
     * Returns an object that describes the animation a scalable component
     * should go through.
     * @param comp
     * @return
     */
    public void animate (JSLayeredPane pb);
    
    public void animate (ISComponent comp);
}
