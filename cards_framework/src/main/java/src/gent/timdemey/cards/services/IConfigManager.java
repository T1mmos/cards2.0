package gent.timdemey.cards.services;

import gent.timdemey.cards.services.configman.ConfigKey;

public interface IConfigManager
{
    public <T> T get(ConfigKey<T> key);
}
