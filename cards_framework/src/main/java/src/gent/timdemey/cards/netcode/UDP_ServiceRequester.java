package gent.timdemey.cards.netcode;

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
import java.util.function.Consumer;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;

/**
 * Broadcast a string on the network via UDP and listens for string answers. The
 * thread created runs until no more answers are received after a timeout
 * period. Users of this class receive answers (in string form) via a callback.
 * 
 * @author Timmos
 */
public final class UDP_ServiceRequester
{
    private static String UDP_SERVICE_REQUEST_THREAD_NAME = "UDP Service Requester";
    private static int UDP_SERVICE_REQUEST_TIMEOUT = 10000;

    private DatagramSocket dsocket = null;
    private Thread thread = null;
    private final Consumer<String> callback;
    private final String message;

    public UDP_ServiceRequester(String message, Consumer<String> callback)
    {
        this.message = message;
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
        try
        {
            dsocket = new DatagramSocket();
            dsocket.setBroadcast(true);
            dsocket.setSoTimeout(UDP_SERVICE_REQUEST_TIMEOUT);
        }
        catch (SocketException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        thread = new Thread(this::requestLoop, UDP_SERVICE_REQUEST_THREAD_NAME);
        thread.start();
        Services.get(ILogManager.class).log("Thread '" + UDP_SERVICE_REQUEST_THREAD_NAME + "' started.");
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
        Services.get(ILogManager.class).log("This thread has started.");
        try
        {
            // json to raw
            byte[] buffer_out = message.getBytes(IoConstants.UDP_CHARSET);

            // send on each broadcast address
            Services.get(ILogManager.class).log("Sending UDP broadcast messages...");
            for (InetAddress inetAddress : listAllBroadcastAddresses())
            {
                DatagramPacket packet_out = new DatagramPacket(buffer_out, buffer_out.length, inetAddress, 9000);
                dsocket.send(packet_out);
            }

            // now keep reading answers, after 60 seconds of nothing, socket will close
            while (true)
            {
                // receive raw
                byte[] buffer_in = new byte[2000];
                DatagramPacket packet_in = new DatagramPacket(buffer_in, buffer_in.length);

                Services.get(ILogManager.class).log("Waiting for answers...");
                dsocket.receive(packet_in);
                Services.get(ILogManager.class).log("Received some UDP data");

                // raw to json
                byte[] received = packet_in.getData();
                String rcvd = new String(received, 0, packet_in.getLength(), IoConstants.UDP_CHARSET);

                callback.accept(rcvd);
            }
        }
        catch (SocketException e)
        {
            // this is normal when the socket is closed.
            Services.get(ILogManager.class).log("Socket closed: ending this thread.");
        }
        catch (SocketTimeoutException e)
        {
            // this is normal when no more servers response in the limited time window
            Services.get(ILogManager.class).log("No more servers have responded after %s seconds, ending this thread.",
                    UDP_SERVICE_REQUEST_TIMEOUT / 1000);

        }
        catch (IOException e)
        {
            Services.get(ILogManager.class).log(e);
        }
        finally
        {
            dsocket.close();
        }
    }
}
