package gent.timdemey.cards.model.entities.commands.dialogs;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.dialogs.P_ClearServerList;
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
