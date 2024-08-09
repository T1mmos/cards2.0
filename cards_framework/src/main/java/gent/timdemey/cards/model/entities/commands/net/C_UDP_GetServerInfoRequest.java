package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;

import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.model.entities.state.ServerUDP;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.net.UDP_Source;
import gent.timdemey.cards.model.net.UDP_UnicastMessage;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Command broadcasted within an UDP message over the network, in order to receive
 * responses from available servers.
 * @author Timmos
 */
public class C_UDP_GetServerInfoRequest extends CommandBase<P_UDP_GetServerInfoRequest>
{
    private final StateFactory _StateFactory;
    private final ICardPlugin _CardPlugin;
    private final CommandFactory _CommandFactory;
    
    public C_UDP_GetServerInfoRequest(
        Container container, ICardPlugin cardPlugin, StateFactory stateFactory, CommandFactory commandFactory, 
        P_UDP_GetServerInfoRequest parameters)
    {
        super(container, parameters);
        
        this._CardPlugin = cardPlugin;
        this._StateFactory = stateFactory;
        this._CommandFactory = commandFactory;
    }
    
    @Override
    public CanExecuteResponse canExecute()
    {
        CheckContext(ContextType.Server);
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        CheckContext(ContextType.Server);
        
        ServerTCP server = _State.getServer();                
        ServerUDP udpServer = _StateFactory.CreateServerUDP(server, _CardPlugin.getVersion(), _State.getPlayers().size(), _CardPlugin.getPlayerCount());;
        C_UDP_GetServerInfoResponse udpResponseCmd = _CommandFactory.CreateUDPResponse(udpServer);

        UDP_Source udpSource = getSourceUdp();
        UDP_UnicastMessage msg = new UDP_UnicastMessage(udpSource.inetAddress, udpSource.tcpPort, udpResponseCmd);
        
        C_UDP_GetServerInfoResponse responseCmd = msg.responseCmd;
        String json = _CommandDtoMapper.toJson(udpResponseCmd);
        
        _State.getUdpServiceAnnouncer().sendUnicast(udpSource.inetAddress,  udpSource.tcpPort, json);
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
