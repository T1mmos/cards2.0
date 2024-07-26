package gent.timdemey.cards.model.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import gent.timdemey.cards.logging.Logger;

/**
 * Broadcast a string on the network via UDP and listens for string answers. The
 * thread created runs until no more answers are received after a timeout
 * period. Users of this class receive answers (in string form) via a callback.
 * 
 * @author Timmos
 */
public final class UDP_ServiceRequester
{
    private static String UDP_SERVICE_REQUEST_THREAD_NAME = "UI :: UDP Service Requester";
    private static int UDP_SERVICE_REQUEST_TIMEOUT = 10000;

    private DatagramSocket dsocket = null;
    private Thread thread = null;
    private final Consumer<String> callback;
    private final String message;
    private final int port;
    private final Logger _Logger;

    UDP_ServiceRequester(Logger logger, String message, int port, Consumer<String> callback)
    {
        this._Logger = logger;
        
        this.message = message;
        this.port = port;
        this.callback = callback;
    }

    private List<InetAddress> listAllBroadcastAddresses() throws SocketException
    {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements())
        {
            NetworkInterface networkInterface = interfaces.nextElement();

            if (networkInterface.isLoopback() || !networkInterface.isUp())
            {
                continue;
            }

            networkInterface.getInterfaceAddresses().stream().map(a -> a.getBroadcast()).filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }

    public void start()
    {
        thread = new Thread(this::requestLoop, UDP_SERVICE_REQUEST_THREAD_NAME);
        thread.start();
    }

    public void stop()
    {
        if (dsocket != null)
        {
            dsocket.close();
        }
        if (this.thread != null)
        {
            thread.interrupt();
            thread = null;
        }
    }

    private void requestLoop()
    {
        try
        {
            dsocket = new DatagramSocket();
            dsocket.setBroadcast(true);
            dsocket.setSoTimeout(UDP_SERVICE_REQUEST_TIMEOUT);
        }
        catch (SocketException e)
        {
            _Logger.error("Error while setting up a broadcast UDP socket", e);
            return;
        }

        _Logger.info("This thread has started.");
        try
        {
            // json to raw
            byte[] buffer_out = message.getBytes(IoConstants.UDP_CHARSET);

            // send on each broadcast address
            _Logger.info("Sending UDP broadcast messages...");
            for (InetAddress inetAddress : listAllBroadcastAddresses())
            {
                DatagramPacket packet_out = new DatagramPacket(buffer_out, buffer_out.length, inetAddress, port);
                dsocket.send(packet_out);
            }

            // now keep reading answers, after 60 seconds of nothing, socket will close
            while (true)
            {
                // receive raw
                byte[] buffer_in = new byte[2000];
                DatagramPacket packet_in = new DatagramPacket(buffer_in, buffer_in.length);

                _Logger.info("Waiting for answers...");
                dsocket.receive(packet_in);
                _Logger.info("Received some UDP data");

                // raw to json
                byte[] received = packet_in.getData();
                String rcvd = new String(received, 0, packet_in.getLength(), IoConstants.UDP_CHARSET);

                callback.accept(rcvd);
            }
        }
        catch (SocketException e)
        {
            // this is normal when the socket is closed.
            _Logger.info("Socket closed: ending this thread.");
        }
        catch (SocketTimeoutException e)
        {
            // this is normal when no more servers response in the limited time window
            long seconds = TimeUnit.SECONDS.convert(UDP_SERVICE_REQUEST_TIMEOUT, TimeUnit.MILLISECONDS);
            _Logger.info("No more servers have responded after %s seconds, ending this thread.", seconds);

        }
        catch (IOException e)
        {
            _Logger.error(e);
        }
        finally
        {
            dsocket.close();
        }
    }
}
