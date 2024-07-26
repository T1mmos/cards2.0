package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.state.CommandExecution;
import java.util.ArrayList;
import java.util.List;


import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.utils.Debug;
import java.util.UUID;

public class D_OnReexecutionFail extends DialogCommandBase
{
    public final List<CommandExecution> failedReexecutions;
    private final IFrameService _FrameService;
    private final Loc _Loc;

    public D_OnReexecutionFail(IContextService contextService, IFrameService frameService, Loc loc, UUID id, List<CommandExecution> failedReexecutions)
    {
        super(contextService, id);
        
        this._FrameService = frameService;
        this._Loc = loc;
        
        this.failedReexecutions = new ArrayList<>(failedReexecutions);
    }

    @Override
    protected CanExecuteResponse canShowDialog(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog(Context context, ContextType type, State state)
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
