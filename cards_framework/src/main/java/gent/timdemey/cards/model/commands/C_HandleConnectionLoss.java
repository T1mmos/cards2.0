package gent.timdemey.cards.model.commands;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_HandleConnectionLoss extends CommandBase
{
    public C_HandleConnectionLoss(TCP_Connection connection, UUID id)
    {        
    	// todo
    }

    @Override
    protected boolean canExecute(Context context, ContextType contextType, State state)
    {
        return contextType == ContextType.Client || contextType == ContextType.UI;
    }
    
    @Override
    public void execute(Context context, ContextType contextType, State state) {
        
        if (contextType == ContextType.UI)
        {
            context.removeRemotes();
            context.setServerId(null);
            
            Services.get(IDialogService.class).ShowMessage(Loc.get("dialog_title_connectionLost"), Loc.get("msg_connectionLost"));
        }
        else if (contextType == ContextType.Client)
        {
            context.removeRemotes();
            context.setServerId(null);
            
            reschedule(ContextType.UI);
        }
        else 
        {
            throw new IllegalStateException();
        }
    }
}
