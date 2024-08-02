package gent.timdemey.cards.mock;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.di.Container;

public class MockCardPlugin implements ICardPlugin
{
    
    @Override
    public void installCommonServices(Container c)
    {
        
    }
    
    @Override
    public void installUIServices(Container cb)
    {
        // not necessary, the unit tests must install the required (mock)
        // services
    }

    @Override
    public int getPlayerCount()
    {
        // assume multiplayer
        return 2;
    }

    @Override
    public String getName()
    {
        return "Test-CardPlugin";
    }

    @Override
    public Version getVersion()
    {
        return new Version(1,0);
    }

}
