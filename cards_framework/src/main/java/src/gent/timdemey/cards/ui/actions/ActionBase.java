package gent.timdemey.cards.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.IContextListener;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;

public class ActionBase extends AbstractAction implements IContextListener, IStateListener
{
    private final ActionDescriptor desc;
    
    public final Icon icon_rollover; 

    ActionBase(ActionDescriptor desc, String title)
    {
        super(title);

        this.desc = desc;
        this.icon_rollover = null;
        
        checkEnabled();
    }
    
    ActionBase(ActionDescriptor desc, String title, Icon icon)
    {
        this(desc, title, icon, null);
    }
    
    ActionBase(ActionDescriptor desc, String title, Icon icon, Icon rollover)
    {
        super(title, icon);

        this.desc = desc;
        this.icon_rollover = rollover;
        
        checkEnabled();
    }

    @Override
    public final void actionPerformed(ActionEvent e)
    {
        Services.get(IActionService.class).executeAction(desc);
    }

    protected final void checkEnabled()
    {
        setEnabled(Services.get(IActionService.class).canExecuteAction(desc));
    }

    @Override
    public void onChange(ReadOnlyChange roChange)
    {
        
    }

    @Override
    public void onContextInitialized(ContextType type)
    {
        
    }

    @Override
    public void onContextDropped(ContextType type)
    {
        
    }
}
