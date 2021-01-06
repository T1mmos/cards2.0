package gent.timdemey.cards.services.config;

import java.util.EnumSet;

public final class ConfigKey<T>
{

    private static final EnumSet<ConfigStringKey> USED_KEYS = EnumSet.noneOf(ConfigStringKey.class);

    public static final ConfigKey<Boolean> DEBUG = ConfigKey.from(ConfigStringKey.Debug, false);

    private final ConfigStringKey strkey;
    private final T defValue;

    private ConfigKey(ConfigStringKey strkey, T defValue)
    {
        this.strkey = strkey;
        this.defValue = defValue;
    }

    ConfigStringKey getStringKey()
    {
        return strkey;
    }

    T getDefaultValue()
    {
        return defValue;
    }

    private static <T> ConfigKey<T> from(ConfigStringKey strkey, T defValue)
    {
        if (USED_KEYS.contains(strkey))
        {
            throw new IllegalArgumentException("Already a config key in use with string key '" + strkey + "'");
        }
        USED_KEYS.add(strkey);
        return new ConfigKey<T>(strkey, defValue);
    }
}
