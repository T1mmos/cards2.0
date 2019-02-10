package gent.timdemey.cards.entities;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.multiplayer.CreateServerInfo;
import gent.timdemey.cards.multiplayer.io.IConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionAccepter;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceAnnouncer;

class CommandProcessorServer extends ACommandProcessorAsync {

    public static String THREAD_NAME = "CommandProcessorServer";
    private UDP_ServiceAnnouncer srv_udp_announcer = null;
    TCP_ConnectionPool srv_tcp_connpool = null;
    private TCP_ConnectionAccepter srv_tcp_accepter = null;    
    
    CommandProcessorServer() 
    {
        
    }


    @Override
    protected String getThreadName() 
    {
        return THREAD_NAME;
    }

    @Override
    protected void execute(ICommand command) {
        Services.get(ILogManager.class).log("Processing command '" + command.getClass().getSimpleName() +"'");
        command.execute();
        
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        // UUID localId = context.getLocalId();
        if (command instanceof C_Move || command instanceof C_Composite)
        {
            List<UUID> idsToSend = context.getPlayerIdsExcept(command.getMetaInfo().requestingParty);
            srv_tcp_connpool.broadcast(idsToSend, command.serialize());
        }
        
    }  
    
    void createServer(CreateServerInfo info)
    {
        // construct answer envelope
        int major = Services.get(ICardPlugin.class).getMajorVersion();
        int minor = Services.get(ICardPlugin.class).getMinorVersion();
        
        InetAddress addr = null;
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            addr = socket.getLocalAddress();
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        UUID serverId = Services.get(IContextProvider.class).getContext(ContextType.Server).localPlayerId;
        MetaInfo metaInfo = new MetaInfo(0, 0, serverId);
        C_UDP_HelloClient cmd_out = new C_UDP_HelloClient(metaInfo, info.srvname, addr, info.tcpport, major, minor);
        CommandEnvelope env_out = new CommandEnvelope(cmd_out);
        
        // envelope to json
        String json_out = Json.send(env_out);
        
        srv_udp_announcer = new UDP_ServiceAnnouncer(info.udpport, this::canAcceptUdpMessage, json_out);
        srv_udp_announcer.start();
        
        int playerCount = Services.get(ICardPlugin.class).getPlayerCount();
        C_NotWelcomeClient cmdOnFull = new C_NotWelcomeClient(new MetaInfo(), "Server is full");
        String msgOnFull = Json.send(new CommandEnvelope(cmdOnFull));
        
        srv_tcp_connpool = new TCP_ConnectionPool(playerCount, new ConnListener());
        srv_tcp_accepter = new TCP_ConnectionAccepter(srv_tcp_connpool, info, msgOnFull);
        srv_tcp_accepter.start();
    }
    
    private class ConnListener implements IConnectionListener
    {

        @Override
        public void onTcpMessageReceived(TCP_Connection connection, String message) {
            try {
                ICommand cmd = Json.receive(message).command;
                cmd.setVolatileData(connection);
                
                Services.get(ILogManager.class).log("Received command '" + cmd.getClass().getSimpleName() + "' from " + connection.getRemote());
                cmd.scheduleOn(ContextType.Server);
            } catch (Exception e) {
                Services.get(ILogManager.class).log(e);
            }
        }

        @Override
        public void onTcpConnectionAdded(TCP_Connection connection)
        {
            Services.get(ILogManager.class).log("TCP connection added to "+ connection.getRemote());
            
            int requiredPlayerCount = Services.get(ICardPlugin.class).getPlayerCount();
            
            
        }

        @Override
        public void onTcpConnectionLocallyClosed(TCP_Connection connection, UUID id) 
        {
            
        }

        @Override
        public void onTcpConnectionRemotelyClosed(TCP_Connection connection, UUID id) {
            if (id != null)
            {
                ContextLimited context = Services.get(IContextProvider.class).getContext(ContextType.Server); 
                UUID serverId = context.localPlayerId;
                ICommand cmd = new C_DropPlayer(new MetaInfo(0,0,serverId), id);
                context.commandProcessor.schedule(cmd);
            }
        }
        
    }
    
    private Boolean canAcceptUdpMessage(String incoming)
    {
        // json to envelope
        CommandEnvelope env_in;
        try {
            env_in = Json.receive(incoming);
        } catch (Exception e) {
            Services.get(ILogManager.class).log(e);
            return false;
        }
        
        // Only accepts CommandHelloServer
        if (! (env_in.command instanceof C_UDP_HelloServer))
        {
            return false;
        }
        
        return true;
    }
    
    void stopServer ()
    {
        if (srv_udp_announcer != null)
        {
            srv_udp_announcer.stop();
        }
        if (srv_tcp_accepter != null)
        {
            srv_tcp_accepter.stop();
        }
    }
}
