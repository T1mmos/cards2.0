package gent.timdemey.cards.ui.panels;

import java.util.function.Consumer;

import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptor;

public class PanelInData<IN>
{
    /**
     * The payload of the dialog, data that can be used to initialize the UI.
     */
    public IN payload;
    
    /**
     * Call this function if you want to evaluate the enabled state
     * of the button of the given type at the bottom of this dialog.
     */
    public Consumer<PanelButtonDescriptor> verifyButtonFunc;
    
    /**
     * Call this function if you want to close the dialog.
     */
    public Runnable closeFunc;
}
