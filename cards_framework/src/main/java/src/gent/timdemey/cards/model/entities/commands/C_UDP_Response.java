package gent.timdemey.cards.model.entities.commands;

import java.net.InetAddress;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Response;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.entities.game.payload.P_Server;
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
    public final UUID serverId;
    public final String serverName;
    public final InetAddress inetAddress;
    public final int tcpport;
    public final int majorVersion;
    public final int minorVersion;

    public C_UDP_Response(UUID serverId, String serverName, InetAddress inetAddress, int tcpport, int majorVersion,
            int minorVersion)
    {
        this.serverId = serverId;
        this.serverName = serverName;
        this.inetAddress = inetAddress;
        this.tcpport = tcpport;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public C_UDP_Response(P_UDP_Response pl)
    {
        super(pl);

        this.serverId = pl.serverId;
        this.serverName = pl.serverName;
        this.inetAddress = pl.inetAddress;
        this.tcpport = pl.tcpport;
        this.majorVersion = pl.majorVersion;
        this.minorVersion = pl.minorVersion;
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        if (type == ContextType.Client)
        {
            reschedule(ContextType.UI);
        }
        else if (type == ContextType.UI)
        {
            P_Server pl = new P_Server();
            {
                pl.id = serverId;
                pl.inetAddress = inetAddress;
                pl.tcpport = tcpport;
                pl.serverName = serverName;
            }
            Server server = new Server(pl);
            state.getServers().add(server);
        }
        else
        {
            // this command is sent over the wire by UDP_ServiceAnnouncer
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("serverId", serverId) + 
               Debug.getKeyValue("serverName", serverName) + 
               Debug.getKeyValue("inetAddress", inetAddress.getHostAddress()) + 
               Debug.getKeyValue("tcpport", tcpport) + 
               Debug.getKeyValue("majorVersion", majorVersion) + 
               Debug.getKeyValue("minorVersion", minorVersion);
    }
}
