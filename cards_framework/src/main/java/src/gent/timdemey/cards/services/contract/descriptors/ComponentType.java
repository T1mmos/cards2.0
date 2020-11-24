package gent.timdemey.cards.services.contract.descriptors;

public final class ComponentType
{    
    public final String typeName;
    public final ComponentType subType;
    
    ComponentType (String name)
    {
        this(name, null);
    }
    
    ComponentType (String name, ComponentType subType)
    {
        if (name == null)
        {
            throw new NullPointerException("name");
        }
        
        this.typeName = name;
        this.subType = subType;
    }

    public ComponentType derive(String subType)
    {
        return new ComponentType(typeName, new ComponentType(subType));
    }
    
    public boolean hasTypeName(ComponentType compType)
    {
        return this.typeName.equals(compType.typeName);
    }
       
    public boolean isSubType(ComponentType subType)
    {
        if (subType == null)
        {
            throw new NullPointerException("subType");
        }
        
        if (this.subType == null)
        {
            return false;
        }
        else
        {
            return this.subType.equals(subType);        
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
        result = prime * result + ((subType == null) ? 0 : subType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        ComponentType other = (ComponentType) obj;
        if(typeName == null)
        {
            if(other.typeName != null)
                return false;
        }
        else if(!typeName.equals(other.typeName))
            return false;
        if(subType == null)
        {
            if(other.subType != null)
                return false;
        }
        else if(!subType.equals(other.subType))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "ComponentType [typeName=" + typeName + ", subType=" + subType + "]";
    }
    
    
}
