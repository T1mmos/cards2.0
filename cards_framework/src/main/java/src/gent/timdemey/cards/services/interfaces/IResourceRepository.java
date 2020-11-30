package gent.timdemey.cards.services.interfaces;

import java.io.InputStream;

import gent.timdemey.cards.services.contract.preload.IPreload;

public interface IResourceRepository extends IPreload
{
    public enum ResourceType
    {
        IMAGE,
        SOUND,
        LOCALIZATION,
        FONT
    }
    
    public InputStream getResourceAsStream(ResourceType type, String name);

    public ClassLoader getResourceClassLoader(ResourceType type);
}
