package gent.timdemey.cards.services;

import java.io.InputStream;

public interface IResourceRepository
{
    public enum ResourceType
    {
        IMAGE,
        SOUND,
        LOCALIZATION,
        FONTS
    }
    
    public InputStream getResourceAsStream(ResourceType type, String name);

    public ClassLoader getResourceClassLoader(ResourceType type);
}
