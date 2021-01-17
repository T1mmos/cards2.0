package gent.timdemey.cards.model.entities.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.res.ResourceType;
import gent.timdemey.cards.services.interfaces.IResourceRepository;
import gent.timdemey.cards.utils.RandomUtils;

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
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        
        Properties properties = new Properties();
        
        IResourceRepository resRepo = Services.get(IResourceRepository.class);
        File propFile = resRepo.getResourceAsFile(ResourceType.USERFILE, "cards.properties");   
        
        try 
        {
            if (!propFile.mkdirs())
            {
                throw new FileNotFoundException("Failed to create the file tree needed for file " + propFile.getAbsolutePath());
            }
            
            if (!propFile.exists())
            {
                if (!propFile.createNewFile())
                {
                    throw new FileNotFoundException("Failed to create the file " + propFile.getAbsolutePath());
                }
            }
            
            if (load)
            {
                properties.load(new FileInputStream(propFile));
                
                String name = properties.getProperty(PLAYERNAME, RandomUtils.randomString("Player", 4));
                
                state.setLocalName(name);
                state.setLocalId(UUID.randomUUID());
            }
            else
            {
                properties.setProperty(PLAYERNAME, state.getLocalName());
                
                properties.store(new FileOutputStream(propFile), null);
            }     
        }
        catch (Exception ex)
        {
            Logger.error("Failed to " + (load ? "load" : "save") + " the configuration", ex);
        }        
    }
}
