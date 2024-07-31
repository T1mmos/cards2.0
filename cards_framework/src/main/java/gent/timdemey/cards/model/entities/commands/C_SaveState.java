package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_SaveState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import java.util.UUID;

public class C_SaveState extends CommandBase
{
    private final String playerName;
    private final int serverTcpPort;
    private final int serverUdpPort;
    private final int clientUdpPort;
       
    public C_SaveState (
        IContextService contextService, 
        P_SaveState parameters)
    {
        super(contextService, parameters);
        
        this.playerName = parameters.playerName;
        this.serverTcpPort = parameters.serverTcpPort;
        this.serverUdpPort = parameters.serverUdpPort;
        this.clientUdpPort = parameters.clientUdpPort;
    }
        
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        // player name
        if (playerName != null && !playerName.isEmpty() && !playerName.equals(state.getLocalName()))
        {
            return CanExecuteResponse.yesPerm();
        }
        // server TCP port
        if (serverTcpPort > 1024 && serverTcpPort != state.getConfiguration().getServerTcpPort())
        {
            return CanExecuteResponse.yesPerm();
        }       
        // server UDP port
        if (serverUdpPort > 1024 && serverUdpPort != state.getConfiguration().getServerUdpPort())
        {
            return CanExecuteResponse.yesPerm();
        }
        // client UDP port
        if (clientUdpPort > 1024 && clientUdpPort != state.getConfiguration().getClientUdpPort())
        {
            return CanExecuteResponse.yesPerm();
        }
        
        return CanExecuteResponse.no("No (valid) changes were detected in the settings");
    }
    
    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        state.setLocalName(playerName);
        state.getConfiguration().setServerTcpPort(serverTcpPort);    
        state.getConfiguration().setServerUdpPort(serverUdpPort);   
        state.getConfiguration().setClientUdpPort(clientUdpPort); 
    }
}
