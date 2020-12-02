package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.C_CardsVisibleSwitch;
import gent.timdemey.cards.model.entities.commands.C_FakeSolShowGame;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.interfaces.IContextService;

public class SolShowTestActionService extends ActionService
{
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
