package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Response;
import gent.timdemey.cards.model.entities.state.ServerUDP;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.utils.Debug;
import java.util.UUID;

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
        IContextService contextService, State state,
        P_UDP_Response parameters)
    {
        super(contextService, state, parameters);
        
        this.server = parameters.server;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        CheckContext(type, ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type)
    {
        CheckContext(type, ContextType.UI);

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
