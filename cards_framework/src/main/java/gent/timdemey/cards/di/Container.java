/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.di;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

/**
 *
 * @author Timmos
 */
public class Container
{
    private final Map<Class, Class> _TransientClassesMap;    
    private final Map<Class, Supplier> _SingletonSuppliersMap;
    private final Map<Class, Class> _SingletonClassesMap;
    
    private final Map<Class, Object> _SingletonInstancesMap = new HashMap<>();    
            
    Container (Map<Class, Class> transientClassesMap, Map<Class, Class> singletonClassesMap, Map<Class, Supplier> singletonSuppliersMap)
    {
        _TransientClassesMap = transientClassesMap;
        _SingletonClassesMap = singletonClassesMap;
        _SingletonSuppliersMap = singletonSuppliersMap;
    }
        
    public <I> I Get(Class<I> clazz)
    {    
        // keep track of what we're constructing, to detect circular dependencies
        Stack<Class> constructing = new Stack<>();
        
        return TryGet(clazz, constructing);
    }
    
    private <I> I TryGet(Class<I> clazz, Stack<Class> constructing)
    {
        // the container can also be injected
        if (clazz == Container.class)
        {
            return (I) this;
        }
        
        // try singletons
        {
            I instance = TryGet(clazz, constructing, _SingletonClassesMap, _SingletonSuppliersMap, _SingletonInstancesMap);
            if (instance != null)
            {
                return instance;
            }
        }
        
        // try transients
        {
            I instance = TryGet(clazz, constructing, _TransientClassesMap, null, null);
            if (instance != null)
            {
                return instance;
            }
        }
        
        // try concrete classes
        {
            if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()))
            {
                I instance = TryInstantiate(clazz, constructing);
                if (instance != null)
                {
                    return instance;
                }
            }
        }
        
        // fail
        throw new UnsupportedOperationException("Class " + clazz.getName() + " is not mapped or if it's a concrete type, no suitable constructor was found");
    }
    
    private <I> I TryGet(Class<I> iclazz, Stack<Class> constructing, Map<Class, Class> definitions, Map<Class, Supplier> suppliers, Map<Class, Object> instances)
    {    
        I obj = null;
        if (instances != null)
        {
            obj = (I) instances.get(iclazz);
        }        

        // try instantiating from Class definitions if no instance exists            
        if (obj == null && definitions != null)
        {
            Class<? extends I> concreteCls = definitions.get(iclazz);
            if (concreteCls != null)
            {
                I instance = TryInstantiate(concreteCls, constructing);
                if (instance != null)
                {
                    obj = instance;

                    if (instances != null)
                    {
                        instances.put(iclazz, instance);    
                    }                    
                }
            }            
        }
        
        // try instantiating from suppliers if no instance exists            
        if (obj == null && suppliers != null)
        {
            Supplier<? extends I> supplier = suppliers.get(iclazz);
            if (supplier != null)
            {
                I instance = supplier.get();
                if (instance != null)
                {
                    obj = instance;

                    if (instances != null)
                    {
                        instances.put(iclazz, instance);    
                    }                    
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
    
    private <I, C extends I> I TryInstantiate(Class<C> clazz, Stack<Class> constructing)
    {
        if (clazz != null)
        {
            if (constructing.contains(clazz))
            {
                List<Class> classCycle = new ArrayList<>(constructing);
                classCycle.add(clazz); // completes the cycle
                StringBuilder b = new StringBuilder();
                for (int i = 0; i < classCycle.size() - 1; i++)
                {
                    String line = String.format("%1$-80s -> %2$-80s", classCycle.get(i).getName(), classCycle.get(i + 1).getName());
                    b.append(System.lineSeparator() + "\t" + line);
                }
               
                throw new IllegalArgumentException("Class " + clazz.getName() + " cannot be constructed as a circular dependency was detected: " + b);
            }
            
            // track that we are constructing this class
            constructing.push(clazz);
            
            Constructor<?>[] constructors = clazz.getConstructors();

            I instance = null;
            for (Constructor<?> c : constructors)
            {
                Object obj = TryInstantiate(c, constructing);
                if (obj != null)
                {
                    instance = clazz.cast(obj);
                }
                if (instance != null)
                {
                    break;
                }
            }
            
            // successfully constructed or no suitable constructor found
            constructing.pop();
            
            return instance; 
        }
        
        return null;
    }

    private Object TryInstantiate(Constructor<?> c, Stack<Class> constructing) 
    {
        Object[] params = new Object[c.getParameterCount()];
        for (int x = 0; x < c.getParameterCount(); x++)
        {
            Class<?> paramXCls = c.getParameterTypes()[x];
            Object xObj = TryGet(paramXCls, constructing);
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
