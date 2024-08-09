package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Command stopping a running UDP_ServiceRequester service.
 * @author Timmos
 */
public class C_UDP_StopServerInfoRequestService extends CommandBase<P_UDP_StopServerInfoRequestService>
{
    public C_UDP_StopServerInfoRequestService(
        Container container,
        P_UDP_StopServerInfoRequestService parameters)
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
