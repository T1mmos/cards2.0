package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.C_Disconnect.DisconnectReason;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_OnServerConnectionLost extends CommandBase
{
    public C_OnServerConnectionLost()
    {
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    public void preExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);        

        String title = Loc.get(LocKey.DialogTitle_connectionlost);
        String msg = Loc.get(LocKey.DialogMessage_connectionlost);
        Services.get(IDialogService.class).ShowMessage(title, msg);

        C_Disconnect cmd_leavelobby = new C_Disconnect(DisconnectReason.ConnectionLost);
        schedule(ContextType.UI, cmd_leavelobby);
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
