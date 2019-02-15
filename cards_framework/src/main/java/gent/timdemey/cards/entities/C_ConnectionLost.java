package gent.timdemey.cards.entities;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.services.dialogs.IDialogService;

public class C_ConnectionLost extends ACommandPill {

    C_ConnectionLost()
    {        
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.Meta;
    }

    @Override
    public void execute() {
        
        if (getContextType() == ContextType.UI)
        {
            getThreadContext().removeRemotes();
            getThreadContext().setServerId(null);
            
            Services.get(IDialogService.class).ShowMessage(Loc.get("dialog_title_connectionLost"), Loc.get("msg_connectionLost"));
        }
        else if (getContextType() == ContextType.Client)
        {
            getThreadContext().removeRemotes();
            getThreadContext().setServerId(null);
            
            reschedule(ContextType.UI);
        }
        else 
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public void visitExecuted(IGameEventListener listener) {
        // TODO Auto-generated method stub
        
    }

}
