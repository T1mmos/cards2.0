package gent.timdemey.cards.netcode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.C_UDP_Request;
import gent.timdemey.cards.model.entities.commands.C_UDP_Response;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.ISerializationService;

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
            throw new IllegalStateException(
                    "You should not reuse an UDP_ServiceAnnouncer; create a new instance instead.");
        }
        used = true;

        try
        {
            dsocket = new DatagramSocket(udpport);
            Logger.info("Created UDP socket at port " + udpport);
        }
        catch (SocketException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        ThreadFactory thrFactRcv = new ThreadFactory()
        {
            
            @Override
            public Thread newThread(Runnable runnable)
            {
                Thread thread = new Thread(runnable, "Server :: UDP Receiver");
                thread.setDaemon(true);
                return thread;
            }
        };
        
        ThreadFactory thrFactSnd = new ThreadFactory()
        {
            
            @Override
            public Thread newThread(Runnable runnable)
            {
                Thread thread = new Thread(runnable, "Server :: UDP Sender");
                thread.setDaemon(true);
                return thread;
            }
        };

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
        Logger.info("This thread has started.");
        try
        {
            while (true)
            {
                // receive raw
                byte[] buffer_in = new byte[2000];
                DatagramPacket packet_in = new DatagramPacket(buffer_in, buffer_in.length);

                Logger.info("Waiting for UDP data...");
                dsocket.receive(packet_in);
                Logger.info("Received some data.");

                // raw to json
                byte[] received = packet_in.getData();
                String rcvd = new String(received, 0, packet_in.getLength(), IoConstants.UDP_CHARSET);

                Logger.info("Received some data, trying to parse it as a C_UDP_Request...");
                CommandBase command = null;
                try
                {
                    ISerializationService serServ = Services.get(ISerializationService.class);
                    command = serServ.getCommandDtoMapper().toCommand(rcvd);
                }
                catch (Exception ex)
                {
                }

                if (command == null)
                {
                    Logger.warn("Ignoring the received data as it could not be parsed as a command.");
                    continue;
                }
                if (!(command instanceof C_UDP_Request))
                {
                    Logger.warn("Ignoring the received data as the command is not a C_UDP_Request.");
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
            Logger.info(
                    "Socket has closed (SocketException), ending this thread. The exception is expected so not logging it.");
        }
        catch (IOException e)
        {
            Logger.error("An exception has occured, ending this thread", e);
        }
    }

    public void sendUnicast(UDP_UnicastMessage msg)
    {
        execServSend.execute(() -> send(msg));
    }

    private void send(UDP_UnicastMessage msg)
    {
        try
        {
            C_UDP_Response responseCmd = msg.responseCmd;
            ISerializationService serServ = Services.get(ISerializationService.class);
            String json = serServ.getCommandDtoMapper().toJson(responseCmd);
            byte[] buffer_out = json.getBytes(IoConstants.UDP_CHARSET);
            DatagramPacket packet_out = new DatagramPacket(buffer_out, buffer_out.length, msg.destination, msg.port);
            dsocket.send(packet_out);
        }
        catch (SocketException e)
        {
            Logger.info("Socket has closed, ending this thread. The exception is expected so not logging it.");
        }
        catch (IOException e)
        {
            Logger.error("An exception has occured, ending this thread.", e);
        }
    }
}