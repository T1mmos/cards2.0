package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_StopServiceRequester;
import gent.timdemey.cards.services.context.ContextType;

public class C_UDP_StopServiceRequester extends CommandBase
{
    public C_UDP_StopServiceRequester(
        Container container,
        P_UDP_StopServiceRequester parameters)
    {
        super(container, parameters);
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        CheckContext(ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        CheckContext(ContextType.UI);
        
        if (_State.getUdpServiceRequester() == null)
        {
            throw new IllegalStateException("Already stopped the requesting service.");
        }

        _State.getUdpServiceRequester().stop();
        _State.setUdpServiceRequester(null);
    }

    @Override
    public String toDebugString()
    {
        return "";
    }

}
