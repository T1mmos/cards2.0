package gent.timdemey.cards.services.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;


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
    
    protected final IActionService _ActionService;

    ActionBase(IActionService actionService, ActionDescriptor desc, String title)
    {
        super(title);

        this._ActionService = actionService;
        this.desc = desc;
        this.icon_rollover = null;
        this.chainedActionDescs = null;
    }
    
    ActionBase(IActionService actionService, ActionDescriptor desc, String title, Icon icon)
    {
        this(actionService, desc, title, icon, null);
    }
    
    ActionBase(IActionService actionService, ActionDescriptor desc, String title, Icon icon, Icon rollover)
    {
        super(title, icon);
        
        this._ActionService = actionService;
        this.desc = desc;
        this.icon_rollover = rollover;
        this.chainedActionDescs = null;
    }
    
    private ActionBase(IActionService actionService, ActionDescriptor desc, String title, Icon icon, Icon rollover, ActionDescriptor ... chainedActionDescs)
    {        
        super(title, icon);

        this._ActionService = actionService;
        this.desc = desc;
        this.icon_rollover = rollover;
        this.chainedActionDescs = chainedActionDescs;
    }

    public static ActionBase chain(IActionService actionService, ActionBase first, ActionBase second)
    {
        String title = (String) first.getValue(Action.NAME);
        Icon icon = (Icon) first.getValue(Action.SMALL_ICON);
        ActionDescriptor desc = first.desc;
        Icon rollover = first.icon_rollover;
        ActionBase combined = new ActionBase(actionService, desc, title, icon, rollover, second.desc);
        return combined;
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        _ActionService.executeAction(desc);
        
        if (chainedActionDescs != null)
        {
            for (ActionDescriptor ad : chainedActionDescs)
            {
                _ActionService.executeAction(ad);
            }
        }
    }

    public void checkEnabled()
    {
        setEnabled(_ActionService.canExecuteAction(desc));
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
