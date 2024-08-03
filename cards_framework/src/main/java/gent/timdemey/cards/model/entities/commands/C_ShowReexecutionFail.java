package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.state.CommandExecution;
import java.util.ArrayList;
import java.util.List;


import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_ShowReexecutionFail;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.utils.Debug;

public class C_ShowReexecutionFail extends DialogCommandBase
{
    public final List<CommandExecution> failedReexecutions;
    private final IFrameService _FrameService;
    private final Loc _Loc;

    public C_ShowReexecutionFail(
        Container container, IFrameService frameService, Loc loc, 
        P_ShowReexecutionFail parameters)
    {
        super(container, parameters);
        
        this._FrameService = frameService;
        this._Loc = loc;
        
        this.failedReexecutions = new ArrayList<>(parameters.fails);
    }

    @Override
    protected CanExecuteResponse canShowDialog()
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog()
    {
        String title = _Loc.get(LocKey.DialogTitle_commandundone);
        String msg = _Loc.get(LocKey.DialogMessage_commandundone);
        _FrameService.showMessage(title, msg);
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("commandTotal", failedReexecutions.size());
    }
}
