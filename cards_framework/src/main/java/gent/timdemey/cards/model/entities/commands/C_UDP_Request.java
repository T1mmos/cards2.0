package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.di.Container;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_UDP_Request;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.model.entities.state.ServerUDP;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.net.UDP_Source;
import gent.timdemey.cards.model.net.UDP_UnicastMessage;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.context.ContextType;

public class C_UDP_Request extends CommandBase
{
    private final StateFactory _StateFactory;
    private final ICardPlugin _CardPlugin;
    private final CommandFactory _CommandFactory;
    private final CommandDtoMapper _CommandDtoMapper;
    
    public C_UDP_Request(
        Container container, ICardPlugin cardPlugin, StateFactory stateFactory, CommandFactory commandFactory, CommandDtoMapper commandDtoMapper, 
        P_UDP_Request parameters)
    {
        super(container, parameters);
        
        this._CardPlugin = cardPlugin;
        this._StateFactory = stateFactory;
        this._CommandFactory = commandFactory;
        this._CommandDtoMapper = commandDtoMapper;
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
        C_UDP_Response udpResponseCmd = _CommandFactory.CreateUDPResponse(udpServer);

        UDP_Source udpSource = getSourceUdp();
        UDP_UnicastMessage msg = new UDP_UnicastMessage(udpSource.inetAddress, udpSource.tcpPort, udpResponseCmd);
        
        C_UDP_Response responseCmd = msg.responseCmd;
        String json = _CommandDtoMapper.toJson(udpResponseCmd);
        
        _State.getUdpServiceAnnouncer().sendUnicast(udpSource.inetAddress,  udpSource.tcpPort, json);
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
