package gent.timdemey.cards.model.delta;

import java.util.ArrayList;
import java.util.List;

public class Property<X>
{
    private static List<Property<?>> properties = new ArrayList<>();

    public final Class<X> propertyClazz;
    public final String fullname;
    public final String shortname;

    private Property(Class<X> propertyClazz, String fullname, String shortname)
    {
        this.propertyClazz = propertyClazz;
        this.fullname = fullname;
        this.shortname = shortname;
    }

    public static <X> Property<X> of(Class<?> containingClazz, Class<X> propertyClazz, String propertyName)
    {
        String clazzfullname = containingClazz.getName();
        String clazzshortname = containingClazz.getSimpleName();
        String fullname = clazzfullname + "::" + propertyName;
        String shortname = clazzshortname + "::" + propertyName;

        Property<X> property = new Property<>(propertyClazz, fullname, shortname);
        if (properties.contains(property))
        {
            throw new IllegalArgumentException(
                    "The property " + propertyName + " for Class " + clazzshortname + " is already registered.");
        }

        properties.add(property);
        return property;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Property))
        {
            return false;
        }

        Property<?> other = (Property<?>) obj;

        if (propertyClazz != other.propertyClazz)
        {
            return false;
        }
        if (!this.fullname.equals(other.fullname))
        {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode()
    {
        return fullname.hashCode();
    }

    @Override
    public String toString()
    {
        return shortname;
    }
    
    public String toString(StateValueRef<X> value)
    {
        return shortname + " [" + value.toDebugString() + "]";
    }
}
