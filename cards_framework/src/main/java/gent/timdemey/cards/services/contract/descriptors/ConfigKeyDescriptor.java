package gent.timdemey.cards.services.contract.descriptors;

public final class ConfigKeyDescriptor<T> extends DescriptorBase
{
    public final Class<?> clazz;
    public final T defValue;
    
    ConfigKeyDescriptor(String id, Class<T> clazz, T defValue)
    {
        super(id);
        
        if (clazz == null)
        {
            throw new NullPointerException("clazz");
        }
        
        this.clazz = clazz;
        this.defValue = defValue;
    }
}
