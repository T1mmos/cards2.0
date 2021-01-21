package gent.timdemey.cards.model.entities.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.config.Configuration;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.ConfigKeyDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ConfigKeyDescriptors;
import gent.timdemey.cards.services.contract.descriptors.FileDescriptors;
import gent.timdemey.cards.services.interfaces.IConfigurationService;
import gent.timdemey.cards.services.interfaces.IFileService;

public class C_LoadConfig extends CommandBase
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
           
            // load the properties
            try (InputStream is = new FileInputStream(propFile))
            {
                properties.load(is);
            }            

            // parse the properties into valid, strongly types values
            String pname = parseProperty(properties, ConfigKeyDescriptors.PlayerName);
            int tcpport = parseProperty(properties, ConfigKeyDescriptors.TcpPort);
            int udpport = parseProperty(properties, ConfigKeyDescriptors.UdpPort);
            
            state.setLocalName(pname);
            state.setLocalId(UUID.randomUUID());
            
            Configuration cfg = new Configuration();
            cfg.setTcpPort(tcpport);
            cfg.setUdpPort(udpport);
            state.setConfiguration(cfg);
        }
        catch (Exception ex)
        {
            Logger.error("Failed to load the configuration", ex);
        }        
    }
    
    private <T> T parseProperty(Properties properties, ConfigKeyDescriptor<T> cfgkey)
    {
        String flatprop = properties.getProperty(cfgkey.id); 
        IConfigurationService cfgServ = Services.get(IConfigurationService.class);
        T value = cfgServ.parse(cfgkey, flatprop);
        return value;
    }
}
