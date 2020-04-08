package gent.timdemey.cards.services;

import java.io.InputStream;

public interface IResourceManager
{
    public InputStream getResourceAsStream(String name);

    public ClassLoader getResourceClassLoader();
}
