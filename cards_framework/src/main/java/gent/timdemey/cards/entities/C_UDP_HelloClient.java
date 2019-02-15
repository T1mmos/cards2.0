package gent.timdemey.cards.entities;

import java.net.InetAddress;
import java.net.UnknownHostException;

import gent.timdemey.cards.multiplayer.HelloClientInfo;

/**
 * Command sent over UDP to a client in order to let it know that a server is running 
 * on the network. It contains initial info needed by a client to connect, if possible,
 * to the running game on the server.
 * @author Timmos
 *
 */
class C_UDP_HelloClient extends ACommandPill {
    static class CompactConverter extends ASerializer<C_UDP_HelloClient>
    {
        @Override
        protected void write(SerializationContext<C_UDP_HelloClient> sc) {
            writeString(sc, PROPERTY_SERVER_NAME, sc.src.serverName);
            writeString(sc, PROPERTY_SERVER_INETADDRESS, sc.src.inetAddress.getHostAddress());
            writeInt(sc, PROPERTY_SERVER_TCPPORT, sc.src.tcpport);
            writeInt(sc, PROPERTY_MAJOR, sc.src.majorVersion);
            writeInt(sc, PROPERTY_MINOR, sc.src.minorVersion);
        }

        @Override
        protected C_UDP_HelloClient read(DeserializationContext dc) {
            String serverName = readString(dc, PROPERTY_SERVER_NAME);
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getByName(readString(dc, PROPERTY_SERVER_INETADDRESS));
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int tcpport = readInt(dc, PROPERTY_SERVER_TCPPORT);            
            int majorVersion = readInt(dc, PROPERTY_MAJOR);
            int minorVersion = readInt(dc, PROPERTY_MINOR);
            
            return new C_UDP_HelloClient(serverName, inetAddress, tcpport, majorVersion, minorVersion);
        }        
    }
    
    final String serverName;
    final InetAddress inetAddress;
    final int tcpport;
    final int majorVersion;
    final int minorVersion;
    
    C_UDP_HelloClient(String serverName, InetAddress inetAddress, int tcpport, int majorVersion, int minorVersion)
    {
        this.serverName = serverName;
        this.inetAddress = inetAddress;
        this.tcpport = tcpport;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }
    
    @Override
    public CommandType getCommandType() {
        return CommandType.Meta;
    }

    @Override
    public void visitExecuted(IGameEventListener listener) 
    {
        listener.onHelloClient();
    }

    @Override
    public void execute()
    {        
        ContextType type = getContextType();
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
}
