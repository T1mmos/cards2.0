package gent.timdemey.cards;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

public final class Services
{

    private static class EntryKey
    {
        Class<?> clazz;
        Object param;

        private EntryKey(Class<?> clazz, Object param)
        {
            Preconditions.checkNotNull(clazz);
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

    private Services()
    {
    }

    // accessing from multiple threads should be thread safe, as all
    // services are installed from the EDT, and other threads are spawned
    // only after all writes are done. This is the "Thread start rule"
    // in happens-before.
    private static Map<EntryKey, Object> serviceMap = new HashMap<>();

    public static boolean isInstalled(Class<?> iface)
    {
        return serviceMap.containsKey(iface);
    }

    public static <T> void install(Class<T> iface, T implementation)
    {
        install(iface, null, implementation);
    }

    public static <T> void install(Class<T> iface, Object param, T implementation)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        Preconditions.checkState(!serviceMap.containsKey(iface));

        EntryKey entryKey = new EntryKey(iface, param);
        serviceMap.put(entryKey, implementation);
    }

    public static <T> T get(Class<T> iface)
    {
        return get(iface, null);
    }

    public static <T> T get(Class<T> iface, Object param)
    {
        EntryKey entryKey = new EntryKey(iface, param);

        if (!serviceMap.containsKey(entryKey))
        {
            throw new IllegalStateException("Requested a service of type " + iface
                    + (param != null ? "(parameter=" + param + ")" : "") + ", but none is set.");
        }

        return (T) serviceMap.get(iface);

    }
}
