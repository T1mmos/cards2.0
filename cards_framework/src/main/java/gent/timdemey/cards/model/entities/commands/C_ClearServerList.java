package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_ClearServerList;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;

public class C_ClearServerList extends CommandBase
{
    public C_ClearServerList(
        IContextService contextService, State state, 
        P_ClearServerList parameters)
    {
        super(contextService, state, parameters);
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type)
    {
        if (type == ContextType.UI)
        {
            _State.getUDPServers().clear();
        }
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
