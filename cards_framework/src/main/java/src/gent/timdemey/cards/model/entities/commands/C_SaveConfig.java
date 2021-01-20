package gent.timdemey.cards.model.entities.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Properties;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.defines.ConfigDefines;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.FileDescriptors;
import gent.timdemey.cards.services.interfaces.IFileService;

public class C_SaveConfig extends CommandBase
{
    
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
        
        IFileService fServ = Services.get(IFileService.class);

        File propFile = fServ.getFile(FileDescriptors.USERCFG);
        
        try 
        {
            if (!propFile.exists())
            {
                if (!propFile.mkdirs())
                {
                    throw new FileNotFoundException("Failed to create the file tree needed for file " + propFile.getAbsolutePath());
                }            
            
                if (!propFile.createNewFile())
                {
                    throw new FileNotFoundException("Failed to create the file " + propFile.getAbsolutePath());
                }
            }
            
                       
            properties.setProperty(ConfigDefines.PLAYERNAME, state.getLocalName());
            
            properties.store(new FileOutputStream(propFile), null);
              
        }
        catch (Exception ex)
        {
            Logger.error("Failed to save the configuration", ex);
        }        
    }
}
