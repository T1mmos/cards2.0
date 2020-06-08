package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.configman.ConfigKey;

public interface IConfigManager
{
    public <T> T get(ConfigKey<T> key);
}
