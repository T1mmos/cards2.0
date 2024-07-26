package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.ICardPlugin;

import gent.timdemey.cards.model.entities.commands.C_TCP_NOK.TcpNokReason;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.INetworkService;
import java.util.UUID;

public class C_OnTcpConnectionAdded extends CommandBase
{
    private final TCP_Connection tcpConnection;
    private final ICardPlugin _CardPlugin;
    private final INetworkService _NetworkService;
    private final CommandFactory _CommandFactory;

    C_OnTcpConnectionAdded(
        IContextService contextService,
        CommandFactory commandFactory,
        ICardPlugin cardPlugin,
        INetworkService networkService,
        UUID id, TCP_Connection tcpConnection)
    {
        super(contextService, id);
        
        this._CommandFactory = commandFactory;
        this._CardPlugin = cardPlugin;
        this._NetworkService = networkService;
        
        this.tcpConnection = tcpConnection;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        if(type == ContextType.UI)
        {
            state.getTcpConnectionPool().bindUUID(state.getServerId(), tcpConnection);
        }
        else
        {
            int playerCnt_max = _CardPlugin.getPlayerCount();
            int playerCnt_curr = state.getPlayers().size();
            int reserved = state.getTcpConnectionPool().getHalfConnections();

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
