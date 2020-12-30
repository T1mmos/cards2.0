package gent.timdemey.cards;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.ContextService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.panels.SolShowTestPanelService;
import gent.timdemey.cards.test.mock.MockNetworkService;
import gent.timdemey.cards.ui.actions.IActionService;
import gent.timdemey.cards.ui.actions.SolShowTestActionService;

public class SolShowTestPlugin extends SolShowPlugin 
{
    @Override
    public void installServices(Services services)
    {
        services.installIfAbsent(IContextService.class, () -> new ContextService());
        services.installIfAbsent(IActionService.class, () -> new SolShowTestActionService());
        services.installIfAbsent(INetworkService.class, () -> new MockNetworkService());
        services.installIfAbsent(IPanelService.class, () -> new SolShowTestPanelService());
        
        super.installServices(services);
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
