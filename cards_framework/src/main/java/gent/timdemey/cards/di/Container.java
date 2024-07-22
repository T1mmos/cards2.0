/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Timmos
 */
public class Container
{
    private final Map<Class, Class> _TransientMap;
    private final Map<Class, Object> _SingletonMap;    
        
    Container (Map<Class, Class> transientMap, Map<Class, Object> singletonMap)
    {
        _TransientMap = transientMap;
        _SingletonMap = singletonMap;
    }
        
    public <T> T Get(Class<T> clazz)
    {
        // try singletons
        {
            Object obj = _SingletonMap.get(clazz);
            if (obj != null)
            {
                T casted = clazz.cast(obj);
                return casted;
            }
        }
        
        // try transients
        {
            Class<?> objClazz = _TransientMap.get(clazz);
            if (objClazz != null)
            {
               
                Constructor<?>[] constructors = objClazz.getConstructors();
                 
                T instance = null;
                for (Constructor<?> c : constructors)
                {
                    Object obj = TryInstantiate(c);
                    if (obj != null)
                    {
                        instance = clazz.cast(obj);
                    }
                    if (instance != null)
                    {
                        break;
                    }
                }
                
                if (instance != null)
                {
                    return instance;    
                }
            }
        }
        
        // fail
        throw new UnsupportedOperationException("Class " + clazz.getName() + " is not mapped");
    }

    private Object TryInstantiate(Constructor<?> c) 
    {
        Object[] params = new Object[c.getParameterCount()];
        for (int x = 0; x < c.getParameterCount(); x++)
        {
            Class<?> paramXCls = c.getParameterTypes()[x];
            Object xObj = Get(paramXCls);
            params[x] = xObj;
        }
        
        try 
        {
            return c.newInstance(params);
        } 
        catch (Exception ex) 
        {
            return null;
        }
    }

    public List<Object> GetAll() 
    {
        // add transient instances 
        List<Object> values = new ArrayList<>();
        for(Class<?> clz : _TransientMap.keySet())
        {
            values.add(Get(clz));
        }
        
        // add singletons
        values.addAll(_SingletonMap.values());
        
        return values;
    }
}
