package gent.timdemey.cards.services.resources;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.contract.preload.PreloadOrder;
import gent.timdemey.cards.services.contract.preload.PreloadOrderType;
import gent.timdemey.cards.services.contract.res.ResourceType;
import gent.timdemey.cards.services.interfaces.IResourceRepository;

public class ResourceRepository implements IResourceRepository
{
    private final Map<ResourceType, URLClassLoader> classLoaders = new HashMap<>();
    private final Logger _Logger;

    public ResourceRepository(Logger logger)
    {        
        this._Logger = logger;
    }
    
    protected void buildUrlLists (Map<ResourceType, List<URL>> typeUrls)
    {        
        addUrl(typeUrls, ResourceType.IMAGE, ICardPlugin.class, "/img/");
        addUrl(typeUrls, ResourceType.SOUND, ICardPlugin.class, "/snd/");
        addUrl(typeUrls, ResourceType.LOCALIZATION, ICardPlugin.class, "/loc/");
        addUrl(typeUrls, ResourceType.FONT, ICardPlugin.class, "/fonts/");
    }
    
    protected void addUrl (Map<ResourceType, List<URL>> typeUrls, ResourceType type, Class<?> clazz, String dir)
    {
        URL url = null;
        if (clazz == null)
        {
            throw new IllegalArgumentException("Resources need to be located using a class it is relative to");            
        }
        
        url = clazz.getResource(dir);          
        
        if (url == null)
        {
            _Logger.error("Can't build URL for directory %s (ResourceType=%s, Class=%s)", dir, type, clazz.getSimpleName());
            return;
        }
        
        List<URL> urls = typeUrls.get(type);
        if (urls == null)
        {
            urls = new ArrayList<>();
            typeUrls.put(type, urls);
        }
                
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
            classLoaders.put(type, urlClassLoader);
        }
    }
    
    @Override
    public InputStream getResourceAsStream(ResourceType type, String name)
    {
        URLClassLoader resLoader = getUrlClassLoader(type);

        InputStream is;
        try 
        {
            is = resLoader.getResourceAsStream(name);
        }
        catch (Exception ex)
        {
            _Logger.error(ex);
            is = null;
        }
        if (is == null)
        {
            throw new IllegalArgumentException(
                    "Cannot find resource " + name + " in any of the paths " + Arrays.toString(resLoader.getURLs()));
        }

        return is;
    }
    
    @Override
    public File getResourceAsFile(ResourceType type, String name)
    {
        URLClassLoader resLoader = getUrlClassLoader(type);
        
        URL url = resLoader.getResource(name);
        
        File file = new File(url.getFile());
        return file;
    }

    @Override
    public ClassLoader getResourceClassLoader(ResourceType type)
    {
        return getUrlClassLoader(type);
    }

    private URLClassLoader getUrlClassLoader(ResourceType type)
    {
        URLClassLoader resLoader = classLoaders.get(type);
        if (resLoader == null)
        {
            throw new IllegalArgumentException(
                    "This resource manager currently doesn't support loading resource of type " + type);
        }
        return resLoader;
    }

    @PreloadOrder(order = PreloadOrderType.ISOLATED)
    @Override
    public void preload()
    {
        Map<ResourceType, List<URL>> typeUrls = new HashMap<>();
        buildUrlLists(typeUrls);
        createResourceLoaders(typeUrls);
    }
}
