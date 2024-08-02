package gent.timdemey.cards.model.entities.commands;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_LoadConfig;
import gent.timdemey.cards.model.entities.config.Configuration;
import gent.timdemey.cards.model.entities.config.ConfigurationFactory;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.ConfigKeyDescriptor;
import gent.timdemey.cards.services.contract.descriptors.ConfigKeyDescriptors;
import gent.timdemey.cards.services.contract.descriptors.FileDescriptors;
import gent.timdemey.cards.services.interfaces.IConfigurationService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFileService;

public class C_LoadConfig extends CommandBase
{

    private final IFileService _FileService;
    private final IConfigurationService _ConfigurationService;
    private final ConfigurationFactory _ConfigurationFactory;
    private final Logger _Logger;
    
    public C_LoadConfig(
        IContextService contextService, 
        IFileService fileService,
        IConfigurationService configurationService, 
        ConfigurationFactory configurationFactory,
        Logger logger,
        State state,
        P_LoadConfig parameters)
    { 
        super(contextService, state, parameters);
        
        this._FileService = fileService;
        this._ConfigurationService = configurationService;
        this._ConfigurationFactory = configurationFactory;
        this._Logger = logger;
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        CheckContext(type, ContextType.UI);
        return CanExecuteResponse.yes();
    }

    
    @Override
    protected void execute(Context context, ContextType type)
    {
        CheckContext(type, ContextType.UI);
        
        Properties properties = new Properties();
        
        File propFile = _FileService.getFile(FileDescriptors.USERCFG);   
        
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
            int serverTcpPort = parseProperty(properties, ConfigKeyDescriptors.ServerTcpPort);
            int serverUdpPort = parseProperty(properties, ConfigKeyDescriptors.ServerUdpPort);
            int clientUdpPort = parseProperty(properties, ConfigKeyDescriptors.ClientUdpPort);
            
            _State.setLocalName(pname);
            _State.setLocalId(UUID.randomUUID());
            
            Configuration cfg = _ConfigurationFactory.CreateConfiguration();
            cfg.setServerTcpPort(serverTcpPort);
            cfg.setServerUdpPort(serverUdpPort);
            cfg.setClientUdpPort(clientUdpPort);
            _State.setConfiguration(cfg);
        }
        catch (Exception ex)
        {
            _Logger.error("Failed to load the configuration", ex);
        }        
    }
    
    private <T> T parseProperty(Properties properties, ConfigKeyDescriptor<T> cfgkey)
    {
        String flatprop = properties.getProperty(cfgkey.id); 
        T value = _ConfigurationService.parse(cfgkey, flatprop);
        return value;
    }
}
