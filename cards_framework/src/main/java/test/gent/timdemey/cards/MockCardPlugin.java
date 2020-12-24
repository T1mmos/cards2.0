package gent.timdemey.cards;

import gent.timdemey.cards.model.state.State;

public class MockCardPlugin implements ICardPlugin
{
    @Override
    public void installServices(Services services)
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
        return new State();
    }
}
