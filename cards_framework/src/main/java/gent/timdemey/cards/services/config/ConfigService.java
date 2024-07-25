package gent.timdemey.cards.services.config;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.services.contract.descriptors.ConfigKeyDescriptor;
import gent.timdemey.cards.services.interfaces.IConfigurationService;

public class ConfigService implements IConfigurationService
{

    private final Logger _Logger;
    public ConfigService (Logger logger)
    {
        this._Logger = logger;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T parse(ConfigKeyDescriptor<T> key, String input)
    {
        // no input -> use default value
        if (input == null)
        {
            return key.defValue;
        }
        
        // output type is string -> just return the input 
        
        T value = null;
        try 
        {
            if (key.clazz == String.class)
            {
                value = (T) input;
            }
            if (key.clazz == Integer.class)
            {              
                value = (T) (Integer) Integer.parseInt(input);                
            }
        }
        catch (Exception ex)
        {
            value = key.defValue;
            _Logger.warn("Can't convert '%s' into type %s, falling back to default value '%s'", input, key.clazz, key.defValue);
        }
        
        return value;
    }
    
    
}
