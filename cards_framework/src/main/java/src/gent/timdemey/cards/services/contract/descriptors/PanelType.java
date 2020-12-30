package gent.timdemey.cards.services.contract.descriptors;

public enum PanelType
{
    /**
     * Root panel, there is always exactly 1 showing in the frame. Cannot
     * be closed or hidden except when showing a different root panel.
     */
    Root,
    
    /**
     * Dialog panel, can be stacked on top of each other. When closing one,
     * we return to the topmost open dialog or the root panel. A dialog must
     * be explicitly closed, and blocks input to any panels below it.
     */
    Dialog,
    
    /**
     * Standalone panel that can always be shown e.g. a breadcrumb or a HUD. 
     * Doesn't block input to other panels. Can be shown permanently.
     */
    Overlay
}
