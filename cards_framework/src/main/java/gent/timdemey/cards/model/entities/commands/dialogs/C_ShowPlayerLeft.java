package gent.timdemey.cards.model.entities.commands.dialogs;


import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.services.interfaces.IFrameService;

public class C_ShowPlayerLeft extends DialogCommandBase<P_ShowPlayerLeft>
{
    private final Loc _Loc;
    private final IFrameService _FrameService;
    
    public C_ShowPlayerLeft(
        Container container, IFrameService frameService, Loc loc, 
        P_ShowPlayerLeft parameters)
    {
        super(container, parameters);
        
        this._FrameService = frameService;
        this._Loc = loc;
    }

    @Override
    protected CanExecuteResponse canShowDialog()
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog()
    {
        String title = _Loc.get(LocKey.DialogTitle_playerleft);
        String msg = _Loc.get(LocKey.DialogMessage_playerleft);
        _FrameService.showMessage(title, msg);
    }
}
