package gent.timdemey.cards.services.configman;

import gent.timdemey.cards.services.IConfigManager;

public class ConfigManager implements IConfigManager
{

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
