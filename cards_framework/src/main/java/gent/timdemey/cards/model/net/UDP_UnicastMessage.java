package gent.timdemey.cards.model.net;

import java.net.InetAddress;

import gent.timdemey.cards.model.entities.commands.net.C_UDP_GetServerInfoResponse;

public class UDP_UnicastMessage
{
    public final InetAddress destination;
    public final int port;
    public final C_UDP_GetServerInfoResponse responseCmd;
    
    public UDP_UnicastMessage (InetAddress destination, int port, C_UDP_GetServerInfoResponse responseCmd)
    {
        this.destination = destination;
        this.port = port;
        this.responseCmd = responseCmd;
    }
}
