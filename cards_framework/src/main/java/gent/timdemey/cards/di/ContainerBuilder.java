/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.di;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Timmos
 */
public class ContainerBuilder 
{
    
    private final Map<Class, Class> _TransientMap = new HashMap<>();;
    private final Map<Class, Object> _SingletonMap = new HashMap<>();;    
    
    public ContainerBuilder ()
    {
        
    }
    
    public <T> void AddTransient(Class<T> clazz, Class<? extends T> impl)
    {
        _TransientMap.put(clazz, impl);
    }
    
    public <T> void AddSingleton(Class<T> clazz, T instance)
    {
        _SingletonMap.put(clazz, instance);
    }
    
    public Container Build ()
    {
        Container container = new Container(_TransientMap, _SingletonMap);
        return container;        
    }
}
