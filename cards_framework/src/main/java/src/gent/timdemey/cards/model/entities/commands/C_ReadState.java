package gent.timdemey.cards.model.entities.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Reads some initial state from a configuration file.
 * @author Tim
 */
public class C_ReadState extends CommandBase
{

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        Properties properties = new Properties();
        
        try 
        {
            File file = new File("%appdata%/cards.properties");
            if (!file.exists())
            {
                file.createNewFile();
            }
            
            if (file.exists())
            {
                properties.load(new FileInputStream(file));
            }            
        }
        catch (IOException ex)
        {
        
        }
        
        String name = properties.getProperty("playername", "New player");
        
        state.setLocalName(name);
        state.setLocalId(UUID.randomUUID());
    }
    
    
}
