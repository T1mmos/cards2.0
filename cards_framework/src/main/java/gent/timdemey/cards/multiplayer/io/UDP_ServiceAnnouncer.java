package gent.timdemey.cards.multiplayer.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.entities.commands.C_UDP_Request;
import gent.timdemey.cards.model.entities.commands.C_UDP_Response;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.ContextType;

public final class UDP_ServiceAnnouncer
{
    private final int udpport;

    private boolean used = false;
    private DatagramSocket dsocket = null;
    private Thread execServReceive = null;
    private ExecutorService execServSend = null;

    public UDP_ServiceAnnouncer(int udpport)
    {
        this.udpport = udpport;
    }

    public void start()
    {
        if (used)
        {
            throw new IllegalStateException("You should not reuse an UDP_ServiceAnnouncer; create a new instance instead.");
        }
        used = true;
        
        try
        {
            dsocket = new DatagramSocket(udpport);
            Services.get(ILogManager.class).log("Created UDP socket at port " + udpport);
        }
        catch (SocketException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        ThreadFactory thrFactRcv = new ThreadFactoryBuilder().setNameFormat("UDP ServiceAnnouncer Receive %d").build();
        ThreadFactory thrFactSnd = new ThreadFactoryBuilder().setNameFormat("UDP ServiceAnnouncer Send %d").build();

        execServSend = Executors.newSingleThreadExecutor(thrFactSnd);
        
        execServReceive = thrFactRcv.newThread(() -> receiveLoop());
        execServReceive.start();
    }

    public void stop()
    {
        if (dsocket != null)
        {
            dsocket.close();
        }
        if (execServReceive != null)
        {
            execServReceive.interrupt();
            execServReceive = null;
        }
        if (execServSend != null) 
        {
            execServSend.shutdown();
            execServSend = null;
        }
    }

    private void receiveLoop()
    {
        Services.get(ILogManager.class).log("This thread has started.");
        try
        {
            while (true)
            {
                // receive raw
                byte[] buffer_in = new byte[2000];
                DatagramPacket packet_in = new DatagramPacket(buffer_in, buffer_in.length);

                Services.get(ILogManager.class).log("Waiting for UDP data...");
                dsocket.receive(packet_in);
                Services.get(ILogManager.class).log("Received some data.");

                // raw to json
                byte[] received = packet_in.getData();
                String rcvd = new String(received, 0, packet_in.getLength(), IoConstants.UDP_CHARSET);

                Services.get(ILogManager.class).log("Received some data, trying to parse it as a C_UDP_Request...");
                CommandBase command = null;
                try 
                {
                    command = CommandDtoMapper.toCommand(rcvd);
                }
                catch (Exception ex)
                {
                }

                if (command == null)
                {
                    Services.get(ILogManager.class).log("Ignoring the received data as it could not be parsed as a command.");
                    continue;
                }
                if (!(command instanceof C_UDP_Request))
                {
                    Services.get(ILogManager.class).log("Ignoring the received data as the command is not a C_UDP_Request.");
                    continue;
                }
                
                // schedule the UDP request command
                C_UDP_Request udpRequestCmd = (C_UDP_Request) command;
                UDP_Source udpSource = new UDP_Source(packet_in.getAddress(), packet_in.getPort());
                udpRequestCmd.setSourceUdp(udpSource);
                IContextService ctxtServ = Services.get(IContextService.class);
                ctxtServ.getContext(ContextType.Server).schedule(udpRequestCmd);
            }
        }
        catch (SocketException e)
        {
            Services.get(ILogManager.class).log("Socket has closed, ending this thread.");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Services.get(ILogManager.class).log("An exception has occured, ending this thread");
            Services.get(ILogManager.class).log(e);
        }
    }
    
    public void sendUnicast (UDP_UnicastMessage msg)
    {
        execServSend.execute(() -> send(msg));
    }
    
    private void send(UDP_UnicastMessage msg)
    {
        try
        {            
            C_UDP_Response responseCmd = msg.responseCmd;
            String json = CommandDtoMapper.toJson(responseCmd);
            byte[] buffer_out = json.getBytes(IoConstants.UDP_CHARSET);
            DatagramPacket packet_out = new DatagramPacket(buffer_out, buffer_out.length, msg.destination, msg.port);
            dsocket.send(packet_out);            
        }
        catch (SocketException e)
        {
            Services.get(ILogManager.class).log("Socket has closed, ending this thread.");
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Services.get(ILogManager.class).log("An exception has occured, ending this thread");
            Services.get(ILogManager.class).log(e);
        }
    }
}