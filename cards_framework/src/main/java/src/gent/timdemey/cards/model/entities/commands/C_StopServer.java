package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.netcode.TCP_ConnectionAccepter;
import gent.timdemey.cards.netcode.UDP_ServiceAnnouncer;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_StopServer extends CommandBase
{
    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        if (state.getServerId() == null)
        {
            return CanExecuteResponse.no("There is no ServerId set");
        }
        
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        if (type == ContextType.UI)
        {
            schedule(ContextType.Server, this);
            return;
        }

        UDP_ServiceAnnouncer udpServAnnouncer = state.getUdpServiceAnnouncer();
        if (udpServAnnouncer != null)
        {
            udpServAnnouncer.stop();
            state.setUdpServiceAnnouncer(null);
        }

        TCP_ConnectionAccepter tcpConnAccepter = state.getTcpConnectionAccepter();
        if (tcpConnAccepter != null)
        {
            tcpConnAccepter.stop();
            state.setTcpConnectionAccepter(null);
        }
    }

    @Override
    public String toDebugString()
    {
        return "";
    }
}
