package gent.timdemey.cards.services.contract.descriptors;

public enum PanelType
{    
    /**
     * Frame panel, there is always exactly 1, it cannot be replaced. It is the parent of all content, 
     * and is itself set as the content of the frame.
     */
    Frame,
    
    /**
     * Root panel, there is always exactly 1 showing in the frame. Cannot
     * be closed or hidden except when showing a different root panel.
     */
    Root,
    
    /**
     * Dialog panel, can be stacked on top of each other. Any currently visible panel 
     * is made invisible. When closing one,
     * we return to the topmost open dialog or the root panel. A dialog must
     * be explicitly closed, and blocks input to any panel below it.
     */
    Dialog,
    
    /**
     * Same as Dialog but lower panels are not hidden.
     */
    DialogOverlay,
    
    /**
     * Standalone panel that can always be shown e.g. a breadcrumb or a HUD. 
     * Doesn't block input to other panels. Can be shown permanently.
     */
    Overlay
}
