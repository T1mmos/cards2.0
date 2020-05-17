package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Response;
import gent.timdemey.cards.model.entities.game.UDPServer;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
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
    public final UDPServer server;

    public C_UDP_Response(UDPServer server)
    {
        this.server = server;
    }

    public C_UDP_Response(P_UDP_Response pl)
    {
        super(pl);

        this.server = pl.server;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    protected void preExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);

        if (!state.getUDPServers().contains(server))
        {
            state.getUDPServers().add(server);
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("server", server);
    }
}
