package gent.timdemey.cards.services.action;

import javax.swing.Action;
import javax.swing.KeyStroke;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.C_CardsVisibleSwitch;
import gent.timdemey.cards.model.entities.commands.C_FakeSolShowGame;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.contract.descriptors.SolShowTestActionDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;

public class SolShowTestActionService extends ActionService
{
    @Override
    protected ActionBase createAction(ActionDescriptor desc)
    {
        if (desc == SolShowTestActionDescriptors.ad_fakegame)
        {
            ActionBase action = new ActionBase(desc, "Fake SolShow Game"); 
            action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("ctrl F"));
            return action;
        }
        else if (desc == SolShowTestActionDescriptors.ad_switchcvis)
        {
            return new ActionBase(desc, "Switch Card Visibility");
        }
        
        return super.createAction(desc);
    }
    
    @Override
    public boolean canExecuteAction(ActionDescriptor desc)
    {
        if (desc == SolShowTestActionDescriptors.ad_fakegame ||
            desc == SolShowTestActionDescriptors.ad_switchcvis)
        {
            return true;
        }                
        
        return super.canExecuteAction(desc);        
    }
    
    @Override
    public void executeAction(ActionDescriptor desc)
    {
        IContextService ctxtServ = Services.get(IContextService.class);       
        Context context = ctxtServ.getThreadContext();
        
        if (desc == SolShowTestActionDescriptors.ad_fakegame)
        {
            context.schedule(new C_FakeSolShowGame());
        }
        else if (desc == SolShowTestActionDescriptors.ad_switchcvis)
        {
            context.schedule(new C_CardsVisibleSwitch());
        }                
        else            
        {
            super.executeAction(desc);
        }     
    }
}
