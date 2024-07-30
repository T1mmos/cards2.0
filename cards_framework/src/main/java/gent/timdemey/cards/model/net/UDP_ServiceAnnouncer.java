package gent.timdemey.cards.model.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import gent.timdemey.cards.logging.Logger;
import java.net.InetAddress;

public final class UDP_ServiceAnnouncer
{
    private final int udpport;

    private boolean used = false;
    private DatagramSocket dsocket = null;
    private Thread execServReceive = null;
    private ExecutorService execServSend = null;
    private final Logger _Logger;
    private IUdpMessageListener _MessageListener = null;

    public UDP_ServiceAnnouncer(
        Logger logger,
        int udpport)
    {
        this._Logger = logger;        
        this.udpport = udpport;
    }

    public void start(IUdpMessageListener messageListener)
    {
        if (used)
        {
            throw new IllegalStateException(
                    "You should not reuse an UDP_ServiceAnnouncer; create a new instance instead.");
        }
        used = true;

        this._MessageListener = messageListener;
        
        try
        {
            dsocket = new DatagramSocket(udpport);
            _Logger.info("Created UDP socket at port " + udpport);
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
        _Logger.error("This thread has started.");
        try
        {
            while (true)
            {
                // receive raw
                byte[] buffer_in = new byte[2000];
                DatagramPacket packet_in = new DatagramPacket(buffer_in, buffer_in.length);

                _Logger.error("Waiting for UDP data...");
                dsocket.receive(packet_in);
                _Logger.error("Received some data.");

                // raw to json
                byte[] received = packet_in.getData();
                String rcvd = new String(received, 0, packet_in.getLength(), IoConstants.UDP_CHARSET);

                this._MessageListener.OnMessageReceived(packet_in.getAddress(), packet_in.getPort(), rcvd);
                
             
            }
        }
        catch (SocketException e)
        {
            _Logger.error(
                    "Socket has closed (SocketException), ending this thread. The exception is expected so not logging it.");
        }
        catch (IOException e)
        {
            _Logger.error("An exception has occured, ending this thread", e);
        }
    }

    public void sendUnicast(InetAddress destination, int port, String message)
    {
        execServSend.execute(() -> send(destination, port, message));
    }

    private void send(InetAddress destination, int port, String message)
    {
        try
        {         
            byte[] buffer_out = message.getBytes(IoConstants.UDP_CHARSET);
            DatagramPacket packet_out = new DatagramPacket(buffer_out, buffer_out.length, destination, port);
            dsocket.send(packet_out);
        }
        catch (SocketException e)
        {
           _Logger.error("Socket has closed, ending this thread. The exception is expected so not logging it.");
        }
        catch (IOException e)
        {
            _Logger.error("An exception has occured, ending this thread.", e);
        }
    }
}