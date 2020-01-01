package gent.timdemey.cards.services.execution;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.contract.ICommand;
import gent.timdemey.cards.dto.Json;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.commands.HelloClientCommand;
import gent.timdemey.cards.model.commands.HelloServerCommand;
import gent.timdemey.cards.model.commands.C_Composite;
import gent.timdemey.cards.model.commands.C_CreateServer;
import gent.timdemey.cards.model.commands.C_DenyClient;
import gent.timdemey.cards.model.commands.C_DropPlayer;
import gent.timdemey.cards.model.commands.C_Move;
import gent.timdemey.cards.model.commands.CommandEnvelope;
import gent.timdemey.cards.multiplayer.io.IConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionAccepter;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceAnnouncer;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.state.ContextLimited;

class ServerCommandProcessor extends CommandProcessorBase {

    public static String THREAD_NAME = "CommandProcessorServer";
    private UDP_ServiceAnnouncer srv_udp_announcer = null;
    TCP_ConnectionPool srv_tcp_connpool = null;
    private TCP_ConnectionAccepter srv_tcp_accepter = null;    
    
    ServerCommandProcessor() 
    {
        
    }


    @Override
    protected String getThreadName() 
    {
        return THREAD_NAME;
    }

    @Override
    protected void execute(CommandEnvelope envelope) {
        ICommand command = envelope.command;
        Services.get(ILogManager.class).log("Processing command '" + command.getClass().getSimpleName() +"'");
        command.execute();
        
        Context context = Services.get(IContextService.class).getThreadContext();
        // UUID localId = context.getLocalId();
        if (command instanceof C_Move || command instanceof C_Composite)
        {
            List<UUID> idsToSend = context.getPlayerIdsExcept(envelope.getMetaInfo().requestingParty);
            srv_tcp_connpool.broadcast(idsToSend, envelope.serialize());
        }
        
    }  
    
    void createServer(C_CreateServer info)
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
        
        UUID serverId = Services.get(IContextService.class).getContext(ContextType.Server).localPlayerId;
        HelloClientCommand cmd_out = new HelloClientCommand(info.srvname, addr, info.tcpport, major, minor);
        CommandEnvelope env_out = CommandEnvelope.createCommandEnvelope(cmd_out);
        
        // envelope to json
        String json_out = Json.send(env_out);
        
        srv_udp_announcer = new UDP_ServiceAnnouncer(info.udpport, this::canAcceptUdpMessage, json_out);
        srv_udp_announcer.start();
        
        int playerCount = Services.get(ICardPlugin.class).getPlayerCount();
        C_DenyClient cmdOnFull = new C_DenyClient("Server is full");
        String msgOnFull = Json.send(CommandEnvelope.createCommandEnvelope(cmdOnFull));
        
        srv_tcp_connpool = new TCP_ConnectionPool(playerCount, new ConnListener());
        srv_tcp_accepter = new TCP_ConnectionAccepter(srv_tcp_connpool, info, msgOnFull);
        srv_tcp_accepter.start();
    }
    
    private class ConnListener implements IConnectionListener
    {

        @Override
        public void onTcpMessageReceived(TCP_Connection connection, String message) {
            try {
                CommandEnvelope envelope = Json.receive(message);
                ICommand cmd = envelope.command; 
                cmd.setVolatileData(connection);
                
                Services.get(ILogManager.class).log("Received command '" + cmd.getClass().getSimpleName() + "' from " + connection.getRemote());
                envelope.reschedule(ContextType.Server);
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
                ContextLimited context = Services.get(IContextService.class).getContext(ContextType.Server);
                ICommand cmd = new C_DropPlayer(id);
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
        if (! (env_in.command instanceof HelloServerCommand))
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
