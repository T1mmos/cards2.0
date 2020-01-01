package gent.timdemey.cards.model.commands;

import java.net.InetAddress;

import gent.timdemey.cards.multiplayer.HelloClientInfo;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Command sent over UDP to a client in order to let it know that a server is running 
 * on the network. It contains initial info needed by a client to connect, if possible,
 * to the running game on the server.
 * @author Timmos
 *
 */
final class HelloClientCommand extends CommandBase
{    
    final String serverName;
    final InetAddress inetAddress;
    final int tcpport;
    final int majorVersion;
    final int minorVersion;
    
    HelloClientCommand(String serverName, InetAddress inetAddress, int tcpport, int majorVersion, int minorVersion)
    {
        this.serverName = serverName;
        this.inetAddress = inetAddress;
        this.tcpport = tcpport;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    @Override
    protected boolean canExecuteCore(Context context)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    protected void executeCore(Context context)
    {        
        ContextType type = context.getContextType();
        if (type == ContextType.Client)
        {
            reschedule(ContextType.UI);
        }
        else if (type == ContextType.UI)
        {
            HelloClientInfo info = new HelloClientInfo(serverName, inetAddress, tcpport);
            getThreadContext().getCardGameState().servers.add(info);
        }
        else 
        {
            throw new IllegalStateException("This command cannot run in context type: " + type);
        }        
    }

    @Override
    protected void rollbackCore(Context context)
    {
        throw new UnsupportedOperationException();
    }
}
