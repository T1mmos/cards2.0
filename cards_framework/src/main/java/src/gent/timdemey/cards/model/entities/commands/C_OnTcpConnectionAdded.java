package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.C_TCP_NOK.TcpNokReason;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.netcode.TCP_Connection;
import gent.timdemey.cards.services.INetworkService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_OnTcpConnectionAdded extends CommandBase
{
    private final TCP_Connection tcpConnection;

    public C_OnTcpConnectionAdded(TCP_Connection tcpConnection)
    {
        this.tcpConnection = tcpConnection;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void preExecute(Context context, ContextType type, State state)
    {
        if(type == ContextType.UI)
        {
            state.getTcpConnectionPool().bindUUID(state.getServerId(), tcpConnection);
        }
        else
        {
            int playerCnt_max = Services.get(ICardPlugin.class).getPlayerCount();
            int playerCnt_curr = state.getPlayers().size();
            int reserved = state.getTcpConnectionPool().getHalfConnections();

            CommandBase cmd_response;
            if (playerCnt_curr + reserved >= playerCnt_max)
            {
                cmd_response = new C_TCP_NOK(TcpNokReason.LobbyFull);                
            }
            else
            {
                cmd_response = new C_TCP_OK();
            }
            
            INetworkService netServ = Services.get(INetworkService.class);
            netServ.send(tcpConnection, cmd_response);
        }
    }
}
