package gent.timdemey.cards.services.interfaces;

import java.io.File;
import java.io.InputStream;

import gent.timdemey.cards.services.contract.res.ResourceType;

public interface IResourceRepository
{    
    public InputStream getResourceAsStream(ResourceType type, String name);
    
    public File getResourceAsFile(ResourceType type, String name);

    public ClassLoader getResourceClassLoader(ResourceType type);
}
