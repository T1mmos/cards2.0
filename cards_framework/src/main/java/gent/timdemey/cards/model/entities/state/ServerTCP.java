package gent.timdemey.cards.model.entities.state;

import java.net.InetAddress;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.state.payload.P_ServerTCP;
import gent.timdemey.cards.utils.Debug;
import java.util.UUID;

public class ServerTCP extends EntityBase
{
    public final String serverName;
    public final InetAddress inetAddress;
    public final int tcpport;

    ServerTCP(UUID id, String serverName, InetAddress inetAddress, int tcpport)
    {
        super(id);
        this.serverName = serverName;
        this.inetAddress = inetAddress;
        this.tcpport = tcpport;
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("serverName", serverName) + Debug.getKeyValue("inetAddress", inetAddress.getHostAddress())
                + Debug.getKeyValue("serverTcpPort", tcpport);
    }
}
