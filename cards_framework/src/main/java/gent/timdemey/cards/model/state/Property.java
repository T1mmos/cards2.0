package gent.timdemey.cards.model.state;

import java.util.ArrayList;
import java.util.List;

public class Property
{
    private static List<Property> properties = new ArrayList<>();
 
    private final String fullname;
    private final String shortname;
    
    private Property(String fullname, String shortname)
    {
        this.fullname = fullname;
        this.shortname = shortname;
    }
    
    public static Property of (Class<?> containingClazz, String propertyName)
    {
        String clazzfullname = containingClazz.getName();
        String clazzshortname = containingClazz.getSimpleName();
        String fullname = clazzfullname + "::" + propertyName;
        String shortname = clazzshortname + "::" + propertyName;
        
        Property property = new Property(fullname, shortname);
        if (properties.contains(property))
        {
            throw new IllegalArgumentException("The property "+propertyName+" for Class " + clazzshortname + " is already registered.");
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
        
        Property other = (Property) obj;
        return this.fullname.equals(other.fullname);
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
}
