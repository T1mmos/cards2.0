package gent.timdemey.cards.model.commands;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionAccepter;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceAnnouncer;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_StopServer extends CommandBase
{
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }
    
    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Client);
        
        if (type == ContextType.UI)
        {
            reschedule(ContextType.Server);
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
}
