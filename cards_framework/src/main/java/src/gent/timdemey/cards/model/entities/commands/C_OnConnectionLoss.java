package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_OnConnectionLoss extends CommandBase
{
    public C_OnConnectionLoss(TCP_Connection connection, UUID id)
    {
        // todo
    }

    @Override
    protected boolean canExecute(Context context, ContextType contextType, State state)
    {
        return contextType == ContextType.Client || contextType == ContextType.UI;
    }

    @Override
    public void execute(Context context, ContextType contextType, State state)
    {
        if (contextType == ContextType.UI)
        {
            IContextService ctxtServ = Services.get(IContextService.class);
            ctxtServ.drop(ContextType.Client);
            
            state.getPlayers().removeAll(state.getRemotePlayers());
            state.setServerId(null);
            

            String title = Loc.get(LocKey.DialogTitle_connectionlost);
            String msg = Loc.get(LocKey.DialogMessage_connectionlost);
            Services.get(IDialogService.class).ShowMessage(title, msg);
            
            state.setCommandHistory(null);
            state.setCardGame(null);
        }
        else if (contextType == ContextType.Client)
        {
            state.getTcpConnectionPool().closeAllConnections();
                        
            reschedule(ContextType.UI);
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
