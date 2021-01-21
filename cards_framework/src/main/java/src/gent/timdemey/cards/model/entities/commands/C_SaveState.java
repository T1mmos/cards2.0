package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_SaveState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_SaveState extends CommandBase
{
    private final P_SaveState payload;
    
    public C_SaveState(P_SaveState payload)
    {
        super(payload);
        
        this.payload = payload;
    }
    
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        if (payload.playerName != null && !payload.playerName.isEmpty())
        {
            state.setLocalName(payload.playerName);
        }
        if (payload.serverTcpPort > 1024)
        {
            state.getConfiguration().setServerTcpPort(payload.serverTcpPort);    
        }
        if (payload.serverUdpPort > 1024)
        {
            state.getConfiguration().setServerUdpPort(payload.serverUdpPort);    
        }
        if (payload.clientUdpPort >= 10000)
        {
            state.getConfiguration().setClientUdpPort(payload.clientUdpPort);
        }
    }

}
