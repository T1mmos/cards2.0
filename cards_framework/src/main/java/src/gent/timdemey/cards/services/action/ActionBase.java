package gent.timdemey.cards.services.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.IContextListener;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.interfaces.IActionService;

public class ActionBase extends AbstractAction implements IContextListener, IStateListener
{
    public final ActionDescriptor desc;
    public final Icon icon_rollover;     
    private final ActionDescriptor[] chainedActionDescs;

    ActionBase(ActionDescriptor desc, String title)
    {
        super(title);

        this.desc = desc;
        this.icon_rollover = null;
        this.chainedActionDescs = null;
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
        this.chainedActionDescs = null;
    }
    
    private ActionBase(ActionDescriptor desc, String title, Icon icon, Icon rollover, ActionDescriptor ... chainedActionDescs)
    {        
        super(title, icon);

        this.desc = desc;
        this.icon_rollover = rollover;
        this.chainedActionDescs = chainedActionDescs;
    }

    public static ActionBase chain(ActionBase first, ActionBase second)
    {
        String title = (String) first.getValue(Action.NAME);
        Icon icon = (Icon) first.getValue(Action.SMALL_ICON);
        ActionDescriptor desc = first.desc;
        Icon rollover = first.icon_rollover;
        ActionBase combined = new ActionBase(desc, title, icon, rollover, second.desc);
        return combined;
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        IActionService actServ = Services.get(IActionService.class);
        actServ.executeAction(desc);
        
        if (chainedActionDescs != null)
        {
            for (ActionDescriptor ad : chainedActionDescs)
            {
                actServ.executeAction(ad);
            }
        }
    }

    public void checkEnabled()
    {
        setEnabled(Services.get(IActionService.class).canExecuteAction(desc));
    }

    @Override
    public String toString()
    {
        return "ActionBase { " + desc + "}";
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
