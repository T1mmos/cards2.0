package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.di.Container;

import gent.timdemey.cards.model.entities.commands.C_TCP_NOK.TcpNokReason;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_OnTcpConnectionAdded;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.INetworkService;

public class C_OnTcpConnectionAdded extends CommandBase
{
    private final TCP_Connection tcpConnection;
    private final ICardPlugin _CardPlugin;
    private final INetworkService _NetworkService;
    private final CommandFactory _CommandFactory;

    public C_OnTcpConnectionAdded(
        Container container,
        CommandFactory commandFactory,
        ICardPlugin cardPlugin,
        INetworkService networkService,
        P_OnTcpConnectionAdded parameters)
    {
        super(container, parameters);
        
        this._CommandFactory = commandFactory;
        this._CardPlugin = cardPlugin;
        this._NetworkService = networkService;
        
        this.tcpConnection = parameters.tcpConnection;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        if(_ContextType == ContextType.UI)
        {
            _State.getTcpConnectionPool().bindUUID(_State.getServerId(), tcpConnection);
        }
        else
        {
            int playerCnt_max = _CardPlugin.getPlayerCount();
            int playerCnt_curr = _State.getPlayers().size();
            int reserved = _State.getTcpConnectionPool().getHalfConnections();

            CommandBase cmd_response;
            if (playerCnt_curr + reserved > playerCnt_max)
            {
                cmd_response = _CommandFactory.CreateTCPNOK(TcpNokReason.LobbyFull);                
            }
            else
            {
                cmd_response = _CommandFactory.CreateTCPOK();
            }
            
            _NetworkService.send(tcpConnection, cmd_response);
        }
    }
}
