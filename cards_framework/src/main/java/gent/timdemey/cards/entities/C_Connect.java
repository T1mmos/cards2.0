package gent.timdemey.cards.entities;

import java.net.InetAddress;
import java.net.UnknownHostException;

import gent.timdemey.cards.multiplayer.io.TCP_ConnectionCreator;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;

class C_Connect extends ACommandPill 
{

    static class CompactConverter extends ASerializer<C_Connect>
    {
        @Override
        protected void write(SerializationContext<C_Connect> sc) {
            writeString(sc, PROPERTY_SERVER_INETADDRESS, sc.src.srvInetAddress.getHostAddress());
            writeInt(sc, PROPERTY_SERVER_TCPPORT, sc.src.tcpport);
            writeString(sc, PROPERTY_CLIENT_NAME, sc.src.playerName);
        }

        @Override
        protected C_Connect read(DeserializationContext dc) {
            InetAddress srvInetAddress = null;
            try {
                srvInetAddress = InetAddress.getByName(readString(dc, PROPERTY_SERVER_INETADDRESS));
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int tcpport = readInt(dc, PROPERTY_SERVER_TCPPORT);
            String playerName = readString(dc, PROPERTY_CLIENT_NAME);
                       
            return new C_Connect(srvInetAddress, tcpport, playerName);
        }        
    }
    
    final InetAddress srvInetAddress;
    final int tcpport;
    final String playerName;
        
    C_Connect (InetAddress srvInetAddress, int tcpport, String playerName)
    {
       // this.clientName = clientName;
       // this.clientId = clientId;
        this.srvInetAddress = srvInetAddress;
        this.tcpport = tcpport;
        this.playerName = playerName;
    }
    
    @Override
    public CommandType getCommandType() {
        return CommandType.Meta;
    }

    @Override
    public void execute() {
        ContextType contextType = getContextType();
        
        if (contextType == ContextType.UI)
        {
            getThreadContext().setLocalName(playerName);
            
            reschedule(ContextType.Client);
        }
        else if (contextType == ContextType.Client)
        {
            getThreadContext().setLocalName(playerName);
            CommandProcessorClient cProcessor = getProcessorClient();
                        
            cProcessor.connPool = new TCP_ConnectionPool(1, new CommandProcessorClient.ConnListener());
            TCP_ConnectionCreator creator = new TCP_ConnectionCreator(cProcessor.connPool);
            creator.connect(srvInetAddress, tcpport);
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    @Override
    public void visitExecuted(IGameEventListener listener) {
        // TODO Auto-generated method stub
        
    }
}
