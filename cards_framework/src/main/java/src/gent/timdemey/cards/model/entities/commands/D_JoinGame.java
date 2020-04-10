package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.multiplayer.JoinMultiplayerGameData;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IDialogService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.dialogs.DialogButtonType;
import gent.timdemey.cards.services.dialogs.DialogData;
import gent.timdemey.cards.ui.dialogs.JoinMultiplayerGameDialogContent;

public class D_JoinGame extends CommandBase
{

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return state.getServerId() == null;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        IContextService ctxtServ = Services.get(IContextService.class);
        ctxtServ.initialize(ContextType.Client);

        JoinMultiplayerGameDialogContent content = new JoinMultiplayerGameDialogContent();
        IDialogService diagServ = Services.get(IDialogService.class);
        String title = Loc.get(LocKey.DialogTitle_joingame);
        DialogData<JoinMultiplayerGameData> data = diagServ.ShowAdvanced(title, null, content, DialogButtonType.BUTTONS_OK_CANCEL);

        if(data.closeType == DialogButtonType.Ok)
        {
            C_Connect cmd = new C_Connect(data.payload.server, data.payload.playerName);
            schedule(ContextType.UI, cmd);
        }
        else if(data.closeType == DialogButtonType.Cancel)
        {
            ctxtServ.drop(ContextType.Client);
        }
    }

    @Override
    public String toDebugString()
    {
        return "";
    }

}
