package gent.timdemey.cards.services.resman;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

import gent.timdemey.cards.services.IResourceManager;

public class ResourceManager implements IResourceManager
{

    private final ClassLoader resourceClassLoader;

    public ResourceManager()
    {
        URL locUrl = ResourceManager.class.getResource("/loc/");
        URL imgUrl = ResourceManager.class.getResource("/img/");
        URL[] urls = { locUrl, imgUrl };

        resourceClassLoader = new URLClassLoader(urls);
    }

    @Override
    public InputStream getResourceAsStream(String name)
    {
        return resourceClassLoader.getResourceAsStream(name);
    }

    @Override
    public ClassLoader getResourceClassLoader()
    {
        return resourceClassLoader;
    }

}
