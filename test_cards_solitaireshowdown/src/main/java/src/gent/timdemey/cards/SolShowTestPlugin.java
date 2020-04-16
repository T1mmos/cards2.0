package gent.timdemey.cards;

import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IFrameService;
import gent.timdemey.cards.services.context.MockContextService;
import gent.timdemey.cards.services.frame.SolShowTestFrameService;
import gent.timdemey.cards.ui.actions.IActionFactory;
import gent.timdemey.cards.ui.actions.IActionService;
import gent.timdemey.cards.ui.actions.SolShowTestActionFactory;
import gent.timdemey.cards.ui.actions.SolShowTestActionService;

public class SolShowTestPlugin extends SolShowPlugin 
{
    @Override
    public void installServices()
    {
        super.installServices();
        
        App.services.install(IContextService.class, new MockContextService());
        App.services.install(IActionService.class, new SolShowTestActionService());
        App.services.install(IFrameService.class, new SolShowTestFrameService());
    }
    
    @Override
    public void installUiServices()
    {
        super.installUiServices();
        App.services.install(IActionFactory.class, new SolShowTestActionFactory());        
    }
    
    @Override
    public String getName()
    {
        return "SolShow TEST";
    }
    
    @Override
    public int getMajorVersion()
    {
        return 6;
    }
    
    @Override
    public int getMinorVersion()
    {
        return 66;
    }
}
