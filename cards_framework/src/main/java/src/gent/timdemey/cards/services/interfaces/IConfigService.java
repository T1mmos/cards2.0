package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.services.config.ConfigKey;

public interface IConfigService
{
    public <T> T get(ConfigKey<T> key);
}
