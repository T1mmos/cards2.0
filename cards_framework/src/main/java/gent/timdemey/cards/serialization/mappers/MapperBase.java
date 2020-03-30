package gent.timdemey.cards.serialization.mappers;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

final class MapperBase implements IMapper
{
    private static class MappersKey
    {
        private Class<?> srcClazz;
        private Class<?> dstClazz;

        private MappersKey(Class<?> srcClazz, Class<?> dstClazz)
        {
            if (srcClazz == null)
            {
                throw new NullPointerException();
            }
            if (dstClazz == null)
            {
                throw new NullPointerException();
            }
            this.srcClazz = srcClazz;
            this.dstClazz = dstClazz;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null)
            {
                return false;
            }
            if (!(obj instanceof MappersKey))
            {
                return false;
            }

            MappersKey other = (MappersKey) obj;
            if (!srcClazz.equals(other.srcClazz))
            {
                return false;
            }
            if (!dstClazz.equals(other.dstClazz))
            {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(srcClazz, dstClazz);
        }
    }

    MapperBase()
    {

    }

    private Map<MappersKey, MappingFunction<?, ?>> _mappers = new HashMap<>();

    @Override
    public final <SRC, DST> DST map(SRC src, Class<DST> dstClazz)
    {
        if (src == null)
        {
            return null;
        }
        Class<?> srcClazz = src.getClass();

        MappersKey bestKey = null;
        for (MappersKey key : _mappers.keySet())
        {
            if (!key.srcClazz.equals(srcClazz))
            {
                continue;
            }
            if (key.dstClazz.equals(dstClazz))
            {
                bestKey = key;
                break;
            }
            if (dstClazz.isAssignableFrom(key.dstClazz))
            {
                if (bestKey == null || bestKey.dstClazz.isAssignableFrom(key.dstClazz))
                {
                    bestKey = key;
                }
            }
        }

        MappingFunction<SRC, DST> func = (MappingFunction<SRC, DST>) _mappers.get(bestKey);
        
        if (func == null)
        {
            throw new UnsupportedOperationException("No mapper installed to map from " + src.getClass().getSimpleName() + " to " + dstClazz.getSimpleName());
        }
        
        DST dst = func.map(src);
        return dst;
    }

    public <SRC, DST> void addMapping(Class<SRC> srcClazz, Class<DST> dstClazz, MappingFunction<SRC, DST> mappingFunc)
    {
        MappersKey key = new MappersKey(srcClazz, dstClazz);
        _mappers.put(key, mappingFunc);
    }

    private <SRC, DST> MappingFunction<SRC, DST> getMapping(Class<SRC> src, Class<DST> dst)
    {
        MappersKey key = new MappersKey(src, dst);
        MappingFunction<SRC, DST> mappingFunc = (MappingFunction<SRC, DST>) _mappers.get(key);
        return mappingFunc;
    }
}
