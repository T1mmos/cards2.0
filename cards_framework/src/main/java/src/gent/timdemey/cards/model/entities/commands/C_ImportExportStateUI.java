package gent.timdemey.cards.model.entities.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
public class C_ImportExportStateUI extends CommandBase
{
    private final static String PLAYERNAME = "player.name";
    
    public final boolean load;
    
    public C_ImportExportStateUI(boolean load)
    {
        this.load = load;
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        
        Properties properties = new Properties();
        
        try 
        {
            String appdata = System.getenv("APPDATA");
            File rootdir = new File(appdata + "/cards/");
            rootdir.mkdirs();
            File propFile = new File(rootdir, "cards.properties");
            if (!propFile.exists())
            {
                propFile.createNewFile();
            }
            
            if (load)
            {
                if (propFile.exists())
                {   
                    properties.load(new FileInputStream(propFile));
                }   
                
                String name = properties.getProperty(PLAYERNAME, "New player");
                
                state.setLocalName(name);
                state.setLocalId(UUID.randomUUID());
            }
            else
            {
                properties.setProperty(PLAYERNAME, state.getLocalName());
                
                properties.store(new FileOutputStream(propFile), null);
            }                     
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
        
        
    }
}
