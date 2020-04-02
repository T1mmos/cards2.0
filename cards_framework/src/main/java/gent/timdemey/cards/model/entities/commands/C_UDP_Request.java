package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Request;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.UDP_Source;
import gent.timdemey.cards.multiplayer.io.UDP_UnicastMessage;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_UDP_Request extends CommandBase
{    
    public C_UDP_Request ()
    {
        super();
    }
    
    public C_UDP_Request(P_UDP_Request pl)
    {
        super(pl);
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.UI, ContextType.Client);
        
        Server server = state.getServers().get(state.getServerId());
        C_UDP_Response udpResponseCmd = new C_UDP_Response(server.id, server.serverName, server.inetAddress, server.tcpport, 1, 0);
        
        UDP_Source udpSource = getSourceUdp();
        UDP_UnicastMessage msg = new UDP_UnicastMessage(udpSource.inetAddress, udpSource.tcpPort, udpResponseCmd);
        state.getUdpServiceAnnouncer().sendUnicast(msg);
    }
}
