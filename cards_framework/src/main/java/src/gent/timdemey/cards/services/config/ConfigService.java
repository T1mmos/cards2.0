package gent.timdemey.cards.services.config;

import gent.timdemey.cards.services.interfaces.IConfigService;

public class ConfigService implements IConfigService
{
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(ConfigKey<T> key)
    {
        // dummy implementation for now
        if (key.getStringKey() == ConfigStringKey.Debug)
        {
            return (T) Boolean.FALSE;
        }
        return key.getDefaultValue();
    }
}
