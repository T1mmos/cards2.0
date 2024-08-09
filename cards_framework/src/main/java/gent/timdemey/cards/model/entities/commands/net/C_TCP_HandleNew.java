package gent.timdemey.cards.model.entities.commands.net;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.di.Container;

import gent.timdemey.cards.model.entities.commands.net.C_TCP_HandleRejected.TcpNokReason;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.services.context.ContextType;

public class C_TCP_HandleNew extends CommandBase<P_TCP_HandleNew>
{
    private final TCP_Connection tcpConnection;
    private final ICardPlugin _CardPlugin;
    private final CommandFactory _CommandFactory;

    public C_TCP_HandleNew(
        Container container,
        CommandFactory commandFactory,
        ICardPlugin cardPlugin,
        P_TCP_HandleNew parameters)
    {
        super(container, parameters);
        
        this._CommandFactory = commandFactory;
        this._CardPlugin = cardPlugin;
        
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
            _State.getTcpConnectionPool().bindUUID(_State.getServer().id, tcpConnection);
        }
        else
        {
            int playerCnt_max = _CardPlugin.getPlayerCount();
            int playerCnt_curr = _State.getPlayers().size();
            int reserved = _State.getTcpConnectionPool().getHalfConnections();

            CommandBase cmd_response;
            if (playerCnt_curr + reserved > playerCnt_max)
            {
                cmd_response = _CommandFactory.CreateTCPHandleRejected(TcpNokReason.LobbyFull);             
            }
            else
            {
                cmd_response = _CommandFactory.CreateTCPHandleAccepted();
            }
            
            String msg = _CommandDtoMapper.toJson(cmd_response);
            tcpConnection.send(msg);
            
        }
    }
}
