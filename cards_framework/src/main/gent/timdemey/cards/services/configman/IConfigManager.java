package gent.timdemey.cards.services.configman;

public interface IConfigManager {
    public <T> T get(ConfigKey<T> key);
}
