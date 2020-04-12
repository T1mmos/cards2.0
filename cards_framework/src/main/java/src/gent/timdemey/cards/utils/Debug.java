package gent.timdemey.cards.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import gent.timdemey.cards.model.entities.common.EntityBase;

public class Debug
{
    public static String getKeyValue(String key, Object value)
    {
        return "\n    " + key + "=" + value.toString();
    }
    
    public static String listString(String key, List<String> list)
    {
        String listStr = "[" + String.join(",", list) + "]";
        return getKeyValue(key, listStr);
    }
    
    public static String listAny(String key, List<?> list)
    {
        return listAny(key, list, e -> e.toString());
    }
    
    public static <I> String listAny(String key, List<I> list, Function<I, String> func)
    {
        List<String> strlist = new ArrayList<>();
        for (I i : list)
        {
            String istr = func.apply(i);
            strlist.add(istr);
        }
        return listString(key, strlist);
    }
    
    public static <I extends EntityBase> String listEntity(String key, List<I> list)
    {
        return listAny(key, list, e -> e.toDebugString());
    }
}
