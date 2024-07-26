package gent.timdemey.cards.model.net;

import java.net.InetAddress;

import gent.timdemey.cards.model.entities.commands.C_UDP_Response;

public class UDP_UnicastMessage
{
    public final InetAddress destination;
    public final int port;
    public final C_UDP_Response responseCmd;
    
    public UDP_UnicastMessage (InetAddress destination, int port, C_UDP_Response responseCmd)
    {
        this.destination = destination;
        this.port = port;
        this.responseCmd = responseCmd;
    }
}
