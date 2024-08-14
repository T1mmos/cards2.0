package gent.timdemey.cards.services.action;

import gent.timdemey.cards.readonlymodel.ChangeList;
import javax.swing.SwingUtilities;

import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.interfaces.IActionService;

/**
 * Action to start hosting / creating a server.
 * 
 * @author Timmos
 *
 */
class A_CreateMP extends ActionBase
{    
    protected A_CreateMP(IActionService actionService, ActionDescriptor desc, String title)
    {
        super(actionService, desc, title);
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
        // the listener can be invoked from different contexts, 
        // but we want to ensure being on the UI context
        SwingUtilities.invokeLater(() -> checkEnabled());
    }

    @Override
    public void onChanges(ChangeList changeList)
    {
        changeList.OnChange(ReadOnlyState.GameState, this::checkEnabled);
    }
}
