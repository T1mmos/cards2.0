package gent.timdemey.cards;

import gent.timdemey.cards.model.state.State;

public interface ICardPlugin
{
    public void installServices(Services services);
    
    public int getPlayerCount();

    public String getName();

    public int getMajorVersion();

    public int getMinorVersion();

    public State createState();
}
