package gent.timdemey.cards.services.panels;

import java.util.function.Consumer;

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
    public Consumer<PanelButtonType> verifyButtonFunc;
    
    /**
     * Call this function if you want to close the dialog.
     */
    public Runnable closeFunc;
}
