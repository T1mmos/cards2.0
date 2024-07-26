package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import java.util.UUID;

public class D_OnPlayerLeft extends DialogCommandBase
{

    private final Loc _Loc;
    private final IFrameService _FrameService;
    public D_OnPlayerLeft(IContextService contextService, IFrameService frameService, Loc loc, UUID id)
    {
        super(contextService, id);
        
        this._FrameService = frameService;
        this._Loc = loc;
    }

    @Override
    protected CanExecuteResponse canShowDialog(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog(Context context, ContextType type, State state)
    {
        String title = _Loc.get(LocKey.DialogTitle_playerleft);
        String msg = _Loc.get(LocKey.DialogMessage_playerleft);
        _FrameService.showMessage(title, msg);
    }
}
