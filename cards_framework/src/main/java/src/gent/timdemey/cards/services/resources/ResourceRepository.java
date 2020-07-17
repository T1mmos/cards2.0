package gent.timdemey.cards.services.resources;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.services.interfaces.IResourceRepository;

public class ResourceRepository implements IResourceRepository
{
    private final Map<ResourceType, URLClassLoader> resourceLoaders = new HashMap<>();

    public ResourceRepository()
    {        
    }    

    @Override
    public final void loadRepositories()
    {
        Map<ResourceType, List<URL>> typeUrls = new HashMap<>();
        buildUrlLists(typeUrls);
        createResourceLoaders(typeUrls);
    }
    
    protected void buildUrlLists (Map<ResourceType, List<URL>> typeUrls)
    {
        addUrl(typeUrls, ResourceType.IMAGE, ICardPlugin.class, "/img/");
        addUrl(typeUrls, ResourceType.SOUND, ICardPlugin.class, "/snd/");
        addUrl(typeUrls, ResourceType.LOCALIZATION, ICardPlugin.class, "/loc/");
        addUrl(typeUrls, ResourceType.FONT, ICardPlugin.class,"/fonts/");
    }
    
    protected static void addUrl (Map<ResourceType, List<URL>> typeUrls, ResourceType type, Class<?> clazz, String dir)
    {
        List<URL> urls = typeUrls.get(type);
        if (urls == null)
        {
            urls = new ArrayList<>();
            typeUrls.put(type, urls);
        }
        URL url = clazz.getResource(dir);
        urls.add(url);
    }
    
    private void createResourceLoaders(Map<ResourceType, List<URL>> typeUrls)
    {
        for (ResourceType type : typeUrls.keySet())
        {
            List<URL> urls = typeUrls.get(type);
            URL[] urlArray = new URL[urls.size()];
            urls.toArray(urlArray);
            URLClassLoader urlClassLoader = new URLClassLoader(urlArray);
            resourceLoaders.put(type, urlClassLoader);
        }
    }
    
    @Override
    public InputStream getResourceAsStream(ResourceType type, String name)
    {
        URLClassLoader resLoader = getUrlClassLoader(type);

        InputStream is = resLoader.getResourceAsStream(name);
        if (is == null)
        {
            throw new IllegalArgumentException(
                    "Cannot find resource " + name + " in any of the paths " + Arrays.toString(resLoader.getURLs()));
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
