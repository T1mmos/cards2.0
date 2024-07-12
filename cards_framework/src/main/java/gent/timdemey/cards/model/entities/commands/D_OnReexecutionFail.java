package gent.timdemey.cards.model.entities.commands;

import java.util.ArrayList;
import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.utils.Debug;

public class D_OnReexecutionFail extends DialogCommandBase
{
    public final List<CommandExecution> failedReexecutions;

    public D_OnReexecutionFail(List<CommandExecution> failedReexecutions)
    {
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
        String title = Loc.get(LocKey.DialogTitle_commandundone);
        String msg = Loc.get(LocKey.DialogMessage_commandundone);
        Services.get(IFrameService.class).showMessage(title, msg);
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("commandTotal", failedReexecutions.size());
    }
}
