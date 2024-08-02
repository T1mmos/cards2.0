package gent.timdemey.cards;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.di.Container;

public interface ICardPlugin
{
    public void installServices(Container cb);
    
    public int getPlayerCount();

    public String getName();

    public Version getVersion();
}
