package gent.timdemey.cards;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.di.ContainerBuilder;
import gent.timdemey.cards.model.state.State;

public class MockCardPlugin implements ICardPlugin
{
    @Override
    public void installServices(ContainerBuilder cb)
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

    @Override
    public State createState()
    {
        return new State();
    }
}
