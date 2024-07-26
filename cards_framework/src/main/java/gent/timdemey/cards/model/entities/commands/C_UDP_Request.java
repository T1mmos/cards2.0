package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.ICardPlugin;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.ServerTCP;
import gent.timdemey.cards.model.entities.state.ServerUDP;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.entities.state.StateFactory;
import gent.timdemey.cards.model.net.UDP_Source;
import gent.timdemey.cards.model.net.UDP_UnicastMessage;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import java.util.UUID;

public class C_UDP_Request extends CommandBase
{
    private final StateFactory _StateFactory;
    private final ICardPlugin _CardPlugin;
    private final CommandFactory _CommandFactory;
    
    C_UDP_Request(IContextService contextService, ICardPlugin cardPlugin, StateFactory stateFactory, CommandFactory commandFactory, UUID id)
    {
        super(contextService, id);
        
        this._CardPlugin = cardPlugin;
        this._StateFactory = stateFactory;
        this._CommandFactory = commandFactory;
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

        
        ServerTCP server = state.getServer();                
        ServerUDP udpServer = _StateFactory.CreateServerUDP(server, _CardPlugin.getVersion(), state.getPlayers().size(), _CardPlugin.getPlayerCount());;
        C_UDP_Response udpResponseCmd = _CommandFactory.CreateUDPResponse(udpServer);

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
