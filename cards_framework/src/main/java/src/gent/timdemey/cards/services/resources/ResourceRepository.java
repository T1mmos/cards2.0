package gent.timdemey.cards.services.resources;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import gent.timdemey.cards.services.interfaces.IResourceRepository;

public class ResourceRepository implements IResourceRepository
{
    private final Map<ResourceType, URLClassLoader> resourceLoaders = new HashMap<>();

    private static URLClassLoader createResourceLoader(String... dirs)
    {
        URL[] urls = new URL[dirs.length];
        for (int i = 0; i < urls.length; i++)
        {
            URL url = ResourceRepository.class.getResource(dirs[i]);
            urls[i] = url;
        }
        URLClassLoader urlClassLoader = new URLClassLoader(urls);
        return urlClassLoader;
    }

    public ResourceRepository()
    {
        resourceLoaders.put(ResourceType.IMAGE, createResourceLoader("/img/"));
        resourceLoaders.put(ResourceType.SOUND, createResourceLoader("/snd/"));
        resourceLoaders.put(ResourceType.LOCALIZATION, createResourceLoader("/loc/"));
        resourceLoaders.put(ResourceType.FONT, createResourceLoader("/fonts/"));
    }

    @Override
    public InputStream getResourceAsStream(ResourceType type, String name)
    {
        URLClassLoader resLoader = getUrlClassLoader(type);

        InputStream is = resLoader.getResourceAsStream(name);
        if (is == null)
        {
            throw new IllegalArgumentException(
                    "Cannot find resource " + name + " in any of the paths " + resLoader.getURLs());
        }

        return is;
    }

    @Override
    public ClassLoader getResourceClassLoader(ResourceType type)
    {
        return getUrlClassLoader(type);
    }

    private URLClassLoader getUrlClassLoader(ResourceType type)
    {
        URLClassLoader resLoader = resourceLoaders.get(type);
        if (resLoader == null)
        {
            throw new IllegalArgumentException(
                    "This resource manager currently doesn't support loading resource of type " + type);
        }
        return resLoader;
    }
}
