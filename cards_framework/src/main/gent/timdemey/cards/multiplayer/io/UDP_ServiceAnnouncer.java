package gent.timdemey.cards.multiplayer.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.function.Function;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;

public final class UDP_ServiceAnnouncer
{        
    private final int udpport;
    private final String answer;
    private final Function<String, Boolean> acceptor;
    
    private DatagramSocket dsocket = null;
    private Thread thread = null;
    
    public UDP_ServiceAnnouncer(int udpport, Function<String, Boolean> acceptor, String answer)
    {
        this.udpport = udpport;
        this.acceptor = acceptor;
        this.answer = answer;        
    }
    
    public void start() 
    {
        try {
            dsocket = new DatagramSocket(udpport);
        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        thread = new Thread(this::acceptLoop, "UDP Service Announcer");
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
    
    private void acceptLoop()
    {
        Services.get(ILogManager.class).log("This thread has started.");
        try {
            while(true)
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
                
                Boolean accepted = acceptor.apply(rcvd);
                if (accepted == null || !accepted.booleanValue())
                {
                    Services.get(ILogManager.class).log("UDP data is not in the expected format, ignoring");
                    continue;
                }
                
                Services.get(ILogManager.class).log("UDP data is valid, returning an answer to the client");               
                // json to raw
                byte [] buffer_out = answer.getBytes(IoConstants.UDP_CHARSET);
                DatagramPacket packet_out = new DatagramPacket(buffer_out, buffer_out.length, packet_in.getAddress(), packet_in.getPort());
                
                // send the packet
                dsocket.send(packet_out);
            }                
        } catch (SocketException e) {
            Services.get(ILogManager.class).log("Socket has closed, ending this thread.");
            e.printStackTrace();
        } catch (IOException e) {
            Services.get(ILogManager.class).log("An exception has occured, ending this thread");
            Services.get(ILogManager.class).log(e);
        }
    }
}