package gent.timdemey.cards.model.entities.commands.save;

import gent.timdemey.cards.model.entities.commands.payload.P_SaveState;
import gent.timdemey.cards.model.entities.state.State;

public class SaveFunctions
{
    public static boolean canExec_PlayerName(State state, P_SaveState payload)
    {
        return payload.playerName != null && !payload.playerName.isEmpty() && !payload.playerName.equals(state.getLocalName());
    }
    
    public static void exec_PlayerName(State state, P_SaveState payload)
    {
        state.setLocalName(payload.playerName);
    }

    public static boolean canExec_ServerTcpPort(State state, P_SaveState payload)
    {
        return payload.serverTcpPort > 1024 && payload.serverTcpPort != state.getConfiguration().getServerTcpPort();        
    }
    
    public static void exec_ServerTcpPort(State state, P_SaveState payload)
    {
        state.getConfiguration().setServerTcpPort(payload.serverTcpPort);    
    }
    
    public static boolean canExec_ServerUdpPort(State state, P_SaveState payload)
    {
        return payload.serverUdpPort > 1024 && payload.serverUdpPort != state.getConfiguration().getServerUdpPort();          
    }
    
    public static void exec_ServerUdpPort(State state, P_SaveState payload)
    {
        state.getConfiguration().setServerUdpPort(payload.serverUdpPort);    
    }
    
    public static boolean canExec_ClientUdpPort(State state, P_SaveState payload)
    {
        return payload.clientUdpPort > 1024 && payload.clientUdpPort != state.getConfiguration().getClientUdpPort();        
    }
    
    public static void exec_ClientUdpPort(State state, P_SaveState payload)
    {
        state.getConfiguration().setClientUdpPort(payload.clientUdpPort); 
    }
    
    private SaveFunctions()
    {
    }
}
