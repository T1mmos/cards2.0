package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_SaveState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;

public class C_SaveState extends CommandBase
{
    private final String playerName;
    private final int serverTcpPort;
    private final int serverUdpPort;
    private final int clientUdpPort;
       
    public C_SaveState (
        IContextService contextService, State state,
        P_SaveState parameters)
    {
        super(contextService, state, parameters);
        
        this.playerName = parameters.playerName;
        this.serverTcpPort = parameters.serverTcpPort;
        this.serverUdpPort = parameters.serverUdpPort;
        this.clientUdpPort = parameters.clientUdpPort;
    }
        
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        // player name
        if (playerName != null && !playerName.isEmpty() && !playerName.equals(_State.getLocalName()))
        {
            return CanExecuteResponse.yesPerm();
        }
        // server TCP port
        if (serverTcpPort > 1024 && serverTcpPort != _State.getConfiguration().getServerTcpPort())
        {
            return CanExecuteResponse.yesPerm();
        }       
        // server UDP port
        if (serverUdpPort > 1024 && serverUdpPort != _State.getConfiguration().getServerUdpPort())
        {
            return CanExecuteResponse.yesPerm();
        }
        // client UDP port
        if (clientUdpPort > 1024 && clientUdpPort != _State.getConfiguration().getClientUdpPort())
        {
            return CanExecuteResponse.yesPerm();
        }
        
        return CanExecuteResponse.no("No (valid) changes were detected in the settings");
    }
    
    @Override
    protected void execute(Context context, ContextType type)
    {
        _State.setLocalName(playerName);
        _State.getConfiguration().setServerTcpPort(serverTcpPort);    
        _State.getConfiguration().setServerUdpPort(serverUdpPort);   
        _State.getConfiguration().setClientUdpPort(clientUdpPort); 
    }
}
