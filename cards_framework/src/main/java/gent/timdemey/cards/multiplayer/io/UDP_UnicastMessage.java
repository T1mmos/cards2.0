package gent.timdemey.cards.multiplayer.io;

import java.net.InetAddress;

import gent.timdemey.cards.model.commands.C_UDP_Response;

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
