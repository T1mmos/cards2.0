package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.commands.C_NewSolShowGame;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.ContextType;

public class SolShowTestActionService extends ActionService
{
    @Override
    public boolean canExecuteAction(String id)
    {
        if (id.equals(SolShowTestActions.ACTION_FAKESOLSHOWGAME))
        {
            return true;
        }
        
        return super.canExecuteAction(id);
    }
    
    @Override
    public void executeAction(String id)
    {
        switch (id)
        {
            case SolShowTestActions.ACTION_FAKESOLSHOWGAME:
                IContextService ctxtServ = Services.get(IContextService.class);
                if (!ctxtServ.isInitialized(ContextType.Client))
                {
                    ctxtServ.initialize(ContextType.Client); // install a mock executor    
                }                
                ctxtServ.getThreadContext().schedule(new C_NewSolShowGame());
                break;
            default:
                super.executeAction(id);
        }
    }
}
