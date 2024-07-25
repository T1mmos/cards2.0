package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IFrameService;

public class D_OnPlayerLeft extends DialogCommandBase
{
    public D_OnPlayerLeft()
    {
    }

    @Override
    protected CanExecuteResponse canShowDialog(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog(Context context, ContextType type, State state)
    {
        String title = Loc.get(LocKey.DialogTitle_playerleft);
        String msg = Loc.get(LocKey.DialogMessage_playerleft);
        Services.get(IFrameService.class).showMessage(title, msg);
    }
}
