package gent.timdemey.cards.services.resman;

import java.io.InputStream;

public interface IResourceManager {
    public InputStream getResourceAsStream(String name);
    
    public ClassLoader getResourceClassLoader();
}
