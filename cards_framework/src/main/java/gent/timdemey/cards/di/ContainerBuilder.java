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
    private final Map<Class, Class> _TransientClassesMap = new HashMap<>();
   // private final Map<Class, Object> _SingletonInstanceMap = new HashMap<>();  
    private final Map<Class, Class> _SingletonClassesMap = new HashMap<>();
    private final Map<Class, Supplier> _SingletonSupplierMap = new HashMap<>();
    
    public ContainerBuilder ()
    {
        
    }
    
    public <T> void AddTransient(Class<T> clazz, Class<? extends T> impl)
    {
        _TransientClassesMap.put(clazz, impl);
    }
    
    
    public <T> void AddSingleton(Class<T> clazz, Supplier<T> supplier)
    {
        _SingletonSupplierMap.put(clazz, supplier);
    }
    
    public <T> void AddSingleton(Class<T> clazz, Class<? extends T> instanceClass)
    {
        _SingletonClassesMap.put(clazz, instanceClass);
    }
    
    public Container Build ()
    {
        Container container = new Container(_TransientClassesMap, _SingletonClassesMap, _SingletonSupplierMap);
        return container;        
    }
}
