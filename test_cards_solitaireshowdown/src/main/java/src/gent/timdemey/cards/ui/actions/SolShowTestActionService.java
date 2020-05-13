package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.C_CardsVisibleSwitch;
import gent.timdemey.cards.model.entities.commands.C_NewSolShowGame;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;

public class SolShowTestActionService extends ActionService
{
    @Override
    public boolean canExecuteAction(String id)
    {
        switch (id)
        {
            case SolShowTestActions.ACTION_FAKESOLSHOWGAME:
            case SolShowTestActions.ACTION_SWITCHCARDSVISIBLE:
                return true;                
            default:
                return super.canExecuteAction(id);
        }
    }
    
    @Override
    public void executeAction(String id)
    {
        IContextService ctxtServ = Services.get(IContextService.class);       
        Context context = ctxtServ.getThreadContext();
        
        switch (id)
        {
            case SolShowTestActions.ACTION_FAKESOLSHOWGAME:                            
                context.schedule(new C_NewSolShowGame());
                break;
            case SolShowTestActions.ACTION_SWITCHCARDSVISIBLE:     
                context.schedule(new C_CardsVisibleSwitch());
                break;
            default:
                super.executeAction(id);
        }
    }
}
