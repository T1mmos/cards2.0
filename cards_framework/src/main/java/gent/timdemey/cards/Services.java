package gent.timdemey.cards;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

public final class Services {
    
    private Services() 
    {
         
    }
    
    // accessing from multiple threads should be thread safe, as all 
    // services are installed from the EDT, and other threads are spawned
    // only after all writes are done. This is the "Thread start rule" 
    // in happens-before.
    private static Map<Class<?>, Object> serviceMap = new HashMap();
    
    public static boolean isInstalled(Class<?> iface)
    {
        return serviceMap.containsKey(iface);   
    }
    
    public static <T> void install (Class<T> iface, T implementation)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        Preconditions.checkState(!serviceMap.containsKey(iface));
       
        serviceMap.put(iface, implementation);
    }
    
    public static <T> T get(Class<T> iface)
    {
        if (!serviceMap.containsKey(iface))
        {
            throw new IllegalStateException("Requested a service of type " + iface + ", but none is set.");
        }
        
        return (T) serviceMap.get(iface);
    }
}
