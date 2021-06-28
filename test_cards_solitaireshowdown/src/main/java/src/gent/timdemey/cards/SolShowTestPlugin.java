package gent.timdemey.cards;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.action.SolShowTestActionService;
import gent.timdemey.cards.services.cardgame.SolShowTestCardGameService;
import gent.timdemey.cards.services.context.ContextService;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.panels.SolShowTestPanelService;
import gent.timdemey.cards.test.mock.MockNetworkService;

public class SolShowTestPlugin extends SolShowPlugin 
{
    @Override
    public void installServices(Services services)
    {
        services.installIfAbsent(IContextService.class, () -> new ContextService());
        services.installIfAbsent(IActionService.class, () -> new SolShowTestActionService());
        services.installIfAbsent(INetworkService.class, () -> new MockNetworkService());
        services.installIfAbsent(IPanelService.class, () -> new SolShowTestPanelService());
        services.installIfAbsent(ICardGameService.class, () -> new SolShowTestCardGameService());
        
        super.installServices(services);
    }
    
    @Override
    public String getName()
    {
        return "SolShow TEST";
    }
    
    @Override
    public Version getVersion()
    {
        return new Version(1, 0);
    }
        
    @Override
    public State createState()
    {
        return new SolShowTestState();
    }
}
