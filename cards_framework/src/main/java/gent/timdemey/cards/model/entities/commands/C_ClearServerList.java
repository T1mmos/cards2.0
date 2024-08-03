package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_ClearServerList;
import gent.timdemey.cards.services.context.ContextType;

public class C_ClearServerList extends CommandBase
{
    public C_ClearServerList(Container container, P_ClearServerList parameters)
    {
        super(container, parameters);
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        if (_ContextType == ContextType.UI)
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
