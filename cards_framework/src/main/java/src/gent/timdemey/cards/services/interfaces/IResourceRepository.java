package gent.timdemey.cards.services.interfaces;

import java.io.InputStream;

public interface IResourceRepository
{
    public enum ResourceType
    {
        IMAGE,
        SOUND,
        LOCALIZATION,
        FONT
    }
    
    public void loadRepositories();
    
    public InputStream getResourceAsStream(ResourceType type, String name);

    public ClassLoader getResourceClassLoader(ResourceType type);
}
