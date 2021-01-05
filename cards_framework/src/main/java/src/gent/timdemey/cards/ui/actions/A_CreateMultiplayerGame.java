package gent.timdemey.cards.ui.actions;

import javax.swing.SwingUtilities;

import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;

/**
 * Action to start hosting / creating a server.
 * 
 * @author Timmos
 *
 */
class A_CreateMultiplayerGame extends ActionBase
{    
    protected A_CreateMultiplayerGame(ActionDescriptor desc, String title)
    {
        super(desc, title);
    }
    
    @Override
    public void onContextInitialized(ContextType type)
    {
        onContextChange(type);
    }

    @Override
    public void onContextDropped(ContextType type)
    {
        onContextChange(type);
    }
    
    private void onContextChange(ContextType type)
    {
        if (type == ContextType.Server)
        {
            // the listener can be invoked from different contexts, 
            // but we want to ensure being on the UI context
            SwingUtilities.invokeLater(() -> checkEnabled());
        }
    }

    @Override
    public void onChange(ReadOnlyChange roChange)
    {
        if (roChange.property == ReadOnlyState.GameState)
        {
            checkEnabled();
        }
    }
}
