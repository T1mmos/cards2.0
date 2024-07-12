package gent.timdemey.cards.utils;

import java.util.Collection;
import java.util.function.Supplier;

public final class ArrayUtils
{
    private ArrayUtils ()
    {        
    }    
    
    public static <T> T[] from(Collection<T> collection, Supplier<T[]> creator)
    {
        if (collection == null)
        {
            return null;
        }
        
        T[] arr = creator.get();
        T[] arr_actual = collection.toArray(arr);
        return arr_actual;
    }
}
