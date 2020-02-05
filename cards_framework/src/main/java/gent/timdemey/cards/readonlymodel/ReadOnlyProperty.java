package gent.timdemey.cards.readonlymodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gent.timdemey.cards.model.EntityBase;
import gent.timdemey.cards.model.state.Property;

public class ReadOnlyProperty<T>
{
    private static Map<Property<?>, ReadOnlyProperty<?>> KNOWN_PROPERTIES = new HashMap<>();
    
    
    
    static 
    {
        // trigger static initialization of the root class
        String name = ReadOnlyState.class.getName();
        try
        {
            Class.forName(name);
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }
    
    public final Class<T> propertyClazz;
    private final Property<?> wrappee;
    
    private ReadOnlyProperty(Class<T> propertyClazz, Property<?> wrappee)
    {
        this.propertyClazz = propertyClazz;
        this.wrappee = wrappee;
    }
    
    /**
     * Creates a ReadOnlyProperty that wraps around a Property.
     * @param propertyClazz
     * @param property
     * @return
     */
    public static <S extends EntityBase, T extends ReadOnlyEntityBase<S>> ReadOnlyProperty<T> of (Class<T> propertyClazz, Property<S> property)
    {
        // ensure the static initializer has run for the class where the ReadOnlyProperty is declared, to ensure
        // the full hierarchy of statically declared ReadOnlyProperties will register themselves to this class.
        // The only class that must be triggered to statically initialize is then ReadOnlyState.
        try {
            Class.forName(propertyClazz.getName());
        }
        catch (Exception ex)
        {
            throw new IllegalStateException("Expected " + propertyClazz + " to be an available class!");
        }
        return create(propertyClazz, property);
    }
    
    /**
     * Creates a ReadOnlyProperty for any class.
     * @param property
     * @return
     */
    public static <T> ReadOnlyProperty<T> of (Property<T> property)
    {
        return create(property.propertyClazz, property);
    }
    
    private static <T> ReadOnlyProperty<T> create (Class<T> propertyClazz, Property<?> property)
    {
        if (KNOWN_PROPERTIES.containsKey(property))
        {
            throw new IllegalArgumentException("A ReadOnlyProperty for Property " + property.fullname + " already exists!");
        }
        
        ReadOnlyProperty<T> roProperty = new ReadOnlyProperty<T>(propertyClazz, property);
        KNOWN_PROPERTIES.put(property, roProperty);
        
        return roProperty;
    }
    
    public static ReadOnlyProperty<?> getReadOnlyProperty(Property<?> property)
    {
        ReadOnlyProperty<?> roProperty = KNOWN_PROPERTIES.get(property);
        if (roProperty == null)
        {
            throw new IllegalArgumentException("A ReadOnlyProperty for Property " + property.fullname + " is not registered!");
        }
        
        return roProperty;
    }
    
    public TypedChange<T> cast(ReadOnlyChange change)
    {
        if (change.property != this)
        {
            throw new IllegalArgumentException("A ReadOnlyProperty<T> can only produce ReadOnlyChange<T>, but the given ReadOnlyChange<?> was created for a different property " + change.property);
        }
        
        T oldValue = (T) change.oldValue;
        T newValue = (T) change.newValue;
        T addedValue = (T) change.addedValue;
        T removedValue = (T) change.removedValue;
        
        TypedChange<T> changeT = new TypedChange<T>(change.changeType, this, change.entityId, oldValue, newValue, addedValue, removedValue);
        return changeT;
    }
}
