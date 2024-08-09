/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gent.timdemey.cards.model.net;

import gent.timdemey.cards.logging.Logger;
import java.net.Socket;
import java.util.function.Consumer;

/**
 *
 * @author Timmos
 */
public class NetworkFactory 
{
    private final Logger _Logger;
    
    public NetworkFactory (Logger logger)
    {
        this._Logger = logger;        
    }

    TCP_Connection CreateTCPConnection(String name, Socket socket, TCP_ConnectionPool connectionPool)
    {
        return new TCP_Connection(_Logger, name, socket, connectionPool);
    }

    public UDP_ServiceResponder CreateUDPServiceAnnouncer(int udpport) 
    {
        return new UDP_ServiceResponder(_Logger, udpport);
    }

    public TCP_ConnectionPool CreateTCPConnectionPool(String name, int playerCount, ITcpConnectionListener tcpConnListener) 
    {
       return new TCP_ConnectionPool(this, _Logger, name, playerCount, tcpConnListener);
    }

    public TCP_ConnectionAccepter CreateTCPConnectionAccepter(TCP_ConnectionPool tcpConnPool, int tcpport) 
    {
        return new TCP_ConnectionAccepter(_Logger, tcpConnPool, tcpport);
    }
    
    public UDP_ServiceRequester createUDPServiceRequester(String message, int port, Consumer<String> callback)
    {
        return new UDP_ServiceRequester(_Logger, message, port, callback);
    }   
}
