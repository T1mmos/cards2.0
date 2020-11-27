package gent.timdemey.cards;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.ContextService;
import gent.timdemey.cards.services.frame.SolShowTestFrameService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.panels.SolShowTestPanelService;
import gent.timdemey.cards.test.mock.MockNetworkService;
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
        
        App.getServices().install(IContextService.class, new ContextService());
        App.getServices().install(IActionService.class, new SolShowTestActionService());
        App.getServices().install(IFrameService.class, new SolShowTestFrameService());
        App.getServices().install(IPanelService.class, new SolShowTestPanelService());
        App.getServices().install(INetworkService.class, new MockNetworkService());
    }
    
    @Override
    public void installUiServices()
    {
        super.installUiServices();
        App.getServices().install(IActionFactory.class, new SolShowTestActionFactory());        
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
    
    @Override
    public State createState()
    {
        return new SolShowTestState();
    }
}
