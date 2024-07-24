/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.di;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Timmos
 */
public class Container
{
    private final Map<Class, Class> _TransientClassesMap;
    private final Map<Class, Object> _TransientInstancesMap = new HashMap<>();    
    private final Map<Class, Class> _SingletonClassesMap;
    private final Map<Class, Object> _SingletonInstancesMap = new HashMap<>();    
        
    Container (Map<Class, Class> transientClassesMap, Map<Class, Class> singletonClassesMap)
    {
        _TransientClassesMap = transientClassesMap;
        _SingletonClassesMap = singletonClassesMap;
    }
        
    public <I> I Get(Class<I> clazz)
    {
        // try singletons
        {
            I instance = TryGet(clazz, _SingletonClassesMap, _SingletonInstancesMap);
            if (instance != null)
            {
                return instance;
            }
        }
        
        // try transients
        {
            I instance = TryGet(clazz, _TransientClassesMap, _TransientInstancesMap);
            if (instance != null)
            {
                return instance;
            }
        }
        
        // fail
        throw new UnsupportedOperationException("Class " + clazz.getName() + " is not mapped");
    }
    
    private <I> I TryGet(Class<I> iclazz, Map<Class, Class> definitions, Map<Class, Object> instances)
    {        
        I obj = (I) instances.get(iclazz);

        // try instantiating if no instance exists            
        if (obj == null)
        {
            Class<? extends I> concreteCls = definitions.get(iclazz);
            if (concreteCls != null)
            {
                I instance = TryInstantiate(concreteCls);
                if (instance != null)
                {
                    obj = instance;
                    instances.put(iclazz, instance);
                }
            }
        }

        if (obj != null)
        {
            I casted = iclazz.cast(obj);
            return casted;
        }   
        
        return null;
    }
    
    private <I, C extends I> I TryInstantiate(Class<C> clazz)
    {
        if (clazz != null)
        {
            Constructor<?>[] constructors = clazz.getConstructors();

            I instance = null;
            for (Constructor<?> c : constructors)
            {
                Object obj = TryInstantiate(c);
                if (obj != null)
                {
                    instance = clazz.cast(obj);
                }
                if (instance != null)
                {
                    return instance; 
                }
            }
        }
        
        return null;
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
        for(Class<?> clz : _TransientClassesMap.keySet())
        {
            values.add(Get(clz));
        }
        
        // add singletons
        values.addAll(_SingletonInstancesMap.values());
        
        return values;
    }
}
