package gent.timdemey.cards;

import gent.timdemey.cards.common.Version;
import gent.timdemey.cards.di.ContainerBuilder;

public interface ICardPlugin
{
    public void installServices(ContainerBuilder cb);
    
    public int getPlayerCount();

    public String getName();

    public Version getVersion();
}
