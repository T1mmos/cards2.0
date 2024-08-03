package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Response;
import gent.timdemey.cards.model.entities.state.ServerUDP;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

/**
 * Command sent over UDP to a client in order to let it know that a server is
 * running on the network. It contains initial info needed by a client to
 * connect, if possible,g to the running game on the server.
 * 
 * @author Timmos
 *
 */
public final class C_UDP_Response extends CommandBase
{
    public final ServerUDP server;

    public C_UDP_Response(
        Container container,
        P_UDP_Response parameters)
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
