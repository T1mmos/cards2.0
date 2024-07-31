/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.di;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 *
 * @author Timmos
 */
public class ContainerBuilder 
{    
    private final Map<Class, Class> _TransientClassesMap;
    private final Map<Class, Object> _SingletonInstancesMap;
    private final Map<Class, Class> _SingletonClassesMap;
    
    public ContainerBuilder ()
    {
        _TransientClassesMap = new HashMap<>();
        _SingletonInstancesMap = new HashMap<>();  
        _SingletonClassesMap = new HashMap<>();
    }
    
    ContainerBuilder(Map<Class, Class> transientClassesMap, Map<Class, Class> singletonClassesMap, Map<Class, Object> singletonInstancesMap)
    {
        _TransientClassesMap = new HashMap<>(transientClassesMap);
        _SingletonClassesMap = new HashMap<>(singletonClassesMap);
        _SingletonInstancesMap = new HashMap<>(singletonInstancesMap);
    }
    
    public <T> void AddTransient(Class<T> clazz, Class<? extends T> impl)
    {
        _TransientClassesMap.put(clazz, impl);
    }
    
    public <T> void AddSingleton(Class<T> clazz, Class<? extends T> instanceClass)
    {
        _SingletonClassesMap.put(clazz, instanceClass);
    }
    
    public <T> void AddSingleton(Class<T> clazz, T impl)
    {
        _SingletonInstancesMap.put(clazz, impl);
    }
    
    public Container Build ()
    {
        Container container = new Container(_TransientClassesMap, _SingletonClassesMap, _SingletonInstancesMap);
        return container;        
    }
}
