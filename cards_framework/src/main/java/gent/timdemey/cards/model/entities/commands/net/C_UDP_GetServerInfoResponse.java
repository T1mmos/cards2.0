package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.ServerUDP;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

/**
 * Command sent over UDP to a client in order to let it know that a server is
 * running on the network. It contains initial info needed by a client to
 * connect.
 * 
 * @author Timmos
 */
public final class C_UDP_GetServerInfoResponse extends CommandBase<P_UDP_GetServerInfoResponse>
{
    public final ServerUDP server;

    public C_UDP_GetServerInfoResponse(
        Container container,
        P_UDP_GetServerInfoResponse parameters)
    {
        super(container, parameters);
        
        this.server = parameters.server;
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

        if (!_State.getUDPServers().contains(server))
        {
            _State.getUDPServers().add(server);
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("server", server);
    }
}
