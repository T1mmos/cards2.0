package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Request;
import gent.timdemey.cards.model.entities.game.Server;
import gent.timdemey.cards.model.entities.game.UDPServer;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.netcode.UDP_Source;
import gent.timdemey.cards.netcode.UDP_UnicastMessage;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_UDP_Request extends CommandBase
{
    public C_UDP_Request()
    {
        super();
    }

    public C_UDP_Request(P_UDP_Request pl)
    {
        super(pl);
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.Server);
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.Server);

        ICardPlugin cardPlugin = Services.get(ICardPlugin.class);
        
        Server server = state.getServer();                
        UDPServer udpServer = new UDPServer(server, cardPlugin.getVersion(), state.getPlayers().size(), cardPlugin.getPlayerCount());;
        C_UDP_Response udpResponseCmd = new C_UDP_Response(udpServer);

        UDP_Source udpSource = getSourceUdp();
        UDP_UnicastMessage msg = new UDP_UnicastMessage(udpSource.inetAddress, udpSource.tcpPort, udpResponseCmd);
        state.getUdpServiceAnnouncer().sendUnicast(msg);
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
