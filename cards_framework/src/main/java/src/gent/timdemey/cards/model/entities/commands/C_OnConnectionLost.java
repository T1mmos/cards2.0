package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_OnConnectionLost extends CommandBase
{
    public C_OnConnectionLost()
    {
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        return true;
    }

    @Override
    public void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        state.getTcpConnectionPool().closeAllConnections();

        state.getPlayers().removeAll(state.getRemotePlayers());
        state.setServerId(null);

        String title = Loc.get(LocKey.DialogTitle_connectionlost);
        String msg = Loc.get(LocKey.DialogMessage_connectionlost);
        Services.get(IDialogService.class).ShowMessage(title, msg);

        state.setCommandHistory(null);
        state.setCardGame(null);

    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
