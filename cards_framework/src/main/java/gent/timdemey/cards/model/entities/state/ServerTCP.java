package gent.timdemey.cards.model.entities.state;

import java.net.InetAddress;

import gent.timdemey.cards.model.entities.common.EntityBase;
import gent.timdemey.cards.model.entities.state.payload.P_ServerTCP;
import gent.timdemey.cards.utils.Debug;

public class ServerTCP extends EntityBase<P_ServerTCP>
{
    public final String serverName;
    public final InetAddress inetAddress;
    public final int tcpport;

    public ServerTCP(P_ServerTCP parameters)
    {
        super(parameters);
        
        this.serverName = parameters.serverName;
        this.inetAddress = parameters.inetAddress;
        this.tcpport = parameters.tcpport;
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("serverName", serverName) + Debug.getKeyValue("inetAddress", inetAddress.getHostAddress())
                + Debug.getKeyValue("serverTcpPort", tcpport);
    }
}
