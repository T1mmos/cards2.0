package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_SaveState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.panels.settings.Settings;

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
        Settings settings = payload.settingsSupplier.get();
        if (settings.playerName != null && !settings.playerName.isEmpty())
        {
            state.setLocalName(settings.playerName);
        }
        if (settings.serverTcpPort > 1024)
        {
            state.getConfiguration().setServerTcpPort(settings.serverTcpPort);    
        }
        if (settings.serverUdpPort > 1024)
        {
            state.getConfiguration().setServerUdpPort(settings.serverUdpPort);    
        }
        if (settings.clientUdpPort >= 10000)
        {
            state.getConfiguration().setClientUdpPort(settings.clientUdpPort);
        }
    }

}
