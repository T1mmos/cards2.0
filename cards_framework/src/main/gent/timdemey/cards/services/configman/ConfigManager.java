package gent.timdemey.cards.services.configman;

public class ConfigManager implements IConfigManager {

    @Override
    public <T> T get(ConfigKey<T> key) {
        // dummy implementation for now
        if (key.getStringKey() == ConfigStringKey.Debug)
        {
            return (T) Boolean.TRUE;
        }
        return key.getDefaultValue();
    }
}
