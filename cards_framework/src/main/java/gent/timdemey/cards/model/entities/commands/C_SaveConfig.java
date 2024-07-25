package gent.timdemey.cards.model.entities.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import gent.timdemey.cards.ICardPlugin;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.config.Configuration;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.ConfigKeyDescriptors;
import gent.timdemey.cards.services.contract.descriptors.FileDescriptors;
import gent.timdemey.cards.services.interfaces.IFileService;

public class C_SaveConfig extends CommandBase
{
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        return CanExecuteResponse.yesPerm();
    }

    
    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        
        // create a properties object, ensure that content is ordered alphabetically on the keys
        Properties properties = new Properties() 
        {          
            // this is ugly code, might consider to drop Properties entirely and use JSON lib instead
                      
            @Override
            public Set<java.util.Map.Entry<Object, Object>> entrySet()
            {
                // to list
                List<java.util.Map.Entry<Object, Object>> list = new ArrayList<>(super.entrySet());  
                
                // sort the list
                Comparator<java.util.Map.Entry<Object, Object>> comparator = Comparator.comparing( Object::toString, String.CASE_INSENSITIVE_ORDER );
                Collections.sort(list, comparator);
               
                // back to set
                Set<java.util.Map.Entry<Object, Object>> sorted = new LinkedHashSet<>(list);              
               
                return sorted;
            }            
        };
        
        IFileService fServ = Services.get(IFileService.class);

        File propFile = fServ.getFile(FileDescriptors.USERCFG);
                
        try 
        {
            if (!propFile.exists())
            {
                // ensure the parent directory tree exists
                File parent = propFile.getParentFile();
                if (!parent.exists())
                {
                    if (!parent.mkdirs())
                    {
                        throw new FileNotFoundException("Failed to create the file tree needed for file " + propFile.getAbsolutePath());
                    }   
                }         
            
                // ensure the file exists
                if (!propFile.createNewFile())
                {
                    throw new FileNotFoundException("Failed to create the file " + propFile.getAbsolutePath());
                }
            }
            
            // write all properties            
            Configuration cfg = state.getConfiguration();
            properties.setProperty(ConfigKeyDescriptors.PlayerName.id, state.getLocalName());
            properties.setProperty(ConfigKeyDescriptors.ServerTcpPort.id, "" + cfg.getServerTcpPort());
            properties.setProperty(ConfigKeyDescriptors.ServerUdpPort.id, "" + cfg.getServerUdpPort());
            properties.setProperty(ConfigKeyDescriptors.ClientUdpPort.id, "" + cfg.getClientUdpPort());
            
            // create a comment
            ICardPlugin cp = Services.get(ICardPlugin.class);            
            String name = cp.getName();
            String version_str = cp.getVersion().toString();       
            String comment_format = "Last written by %s, %s";
            String comment = String.format(comment_format, name, version_str);
            
            // write the properties file
            try (FileOutputStream os = new FileOutputStream(propFile))
            {
                properties.store(os, comment);    
            }
        }
        catch (Exception ex)
        {
            Logger.error("Failed to save the configuration", ex);
        }        
    }
}
