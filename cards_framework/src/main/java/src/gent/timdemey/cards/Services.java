package gent.timdemey.cards;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public final class Services
{
    public static final class UnavailableServiceException extends RuntimeException
    {
        private UnavailableServiceException (Class<?> iface, Object param)
        {
            super ("Requested a service of type " + iface
            + (param != null ? "(parameter=" + param + ")" : "") + ", but none is set.");
        }
    }
    
    private static class EntryKey
    {
        final Class<?> clazz;
        final Object param;

        private EntryKey(Class<?> clazz, Object param)
        {
            Preconditions.checkNotNull(clazz);
            
            this.clazz = clazz;
            this.param = param;
        }

        @Override
        public int hashCode()
        {
            final int prime = 31;
            int result = 17;
            result = prime * result + clazz.hashCode();
            result = prime * result + ((param == null) ? 0 : param.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null)
            {
                return false;
            }
            if (this == obj)
            {
                return true;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            EntryKey other = (EntryKey) obj;

            if (!clazz.equals(other.clazz))
            {
                return false;
            }

            if (param == null && other.param != null || param != null && other.param == null)
            {
                return false;
            }
            if (param != null && !param.equals(other.param))
            {
                return false;
            }

            return true;
        }

    }
    
    // accessing from multiple threads should be thread safe, as all
    // services are installed from the EDT, and other threads are spawned
    // only after all writes are done. This is the "Thread start rule"
    // in happens-before.
    private final Map<EntryKey, Object> serviceMap = new HashMap<>();

    public Services()
    {
    }

    private static Services get()
    {
        return App.getServices();
    }
    
    public static boolean isInstalled(Class<?> iface)
    {
        Object value = find(iface, null);
        return value != null;
    }

    public <T> void installIfAbsent(Class<T> iface, Supplier<T> creator)
    {
        if(!isInstalled(iface))
        {
            T impl = creator.get();
            install(iface, impl);
        }
    }
    
    public <T> void install(Class<T> iface, T implementation)
    {
        install(iface, null, implementation);
    }

    public <T> void install(Class<T> iface, Object param, T implementation)
    {
        if (iface == null)
        {
            throw new IllegalArgumentException("iface cannot be null");
        }
        if (implementation == null)
        {
            throw new IllegalArgumentException("implementation cannot be null");
        }
        
        EntryKey entryKey = new EntryKey(iface, param);
        if (serviceMap.containsKey(entryKey))
        {
            Object impl = serviceMap.get(entryKey);
            String msg = "Service %s for interface %s (param=%s) cannot be installed: an implementation is already installed: %s";
            String formatted = String.format(msg, implementation.getClass().getSimpleName(), iface.getSimpleName(), param, impl.getClass().getSimpleName());
         //   throw new IllegalStateException(formatted);
        }
        
        serviceMap.put(entryKey, implementation);
    }

    public static <T> T get(Class<T> iface)
    {
        return get(iface, null);
    }

    private static <T> T find(Class<T> iface, Object param)
    {
        EntryKey entryKey = new EntryKey(iface, param);

        Map<EntryKey, Object> map = get().serviceMap;
        T value = (T) map.get(entryKey);
        if (value == null)
        {
            // iterate over the keys, maybe a child interface is installed
            for (EntryKey ek : new HashSet<>(map.keySet()))
            {
                if (iface.isAssignableFrom(ek.clazz) && Objects.equal(param, ek.param))
                {
                    // install a new mapping for the requested interface, to speed up further lookups
                    value = (T) map.get(ek);
                    map.put(entryKey, value);
                    break;
                }
            }
        }
        
        return value;
    }
    
    public static <T> T get(Class<T> iface, Object param)
    {
        T value = find(iface, param);
        
        if (value == null)
        {
            throw new UnavailableServiceException(iface, param);
        }
        
        return value;
    }
    
    public static void preload()
    {
        // take a copy as parent interface request add more EntryKey to the service map. 
        // this solves ConcurrentModificationExceptions and equally important we only want to preload once.
        for (Object obj : new HashSet<>(get().serviceMap.values()))
        {
            if (obj instanceof IPreload)
            {
                ((IPreload) obj).preload();
            }
        }
    }
}
