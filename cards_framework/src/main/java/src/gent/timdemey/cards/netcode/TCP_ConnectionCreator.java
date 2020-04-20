package gent.timdemey.cards.netcode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;

public class TCP_ConnectionCreator
{
    private TCP_ConnectionCreator()
    {
    }

    public static void connect(TCP_ConnectionPool tcpConnPool, InetAddress address, int port)
    {
        Socket socket;
        try
        {
            String hostAddr = address.getHostAddress();
            Services.get(ILogManager.class).log("Connecting to %s:%s", hostAddr, port);
            socket = new Socket(address, port);
            String localAddr = socket.getLocalAddress().getHostAddress();
            int localPort = socket.getLocalPort();
            Services.get(ILogManager.class).log("Connected to %s:%s, local address is %s:%s", hostAddr, port, localAddr, localPort);
            tcpConnPool.addConnection(socket);
        }
        catch (IOException e)
        {
            Services.get(ILogManager.class).log(e);
        }
    }
}
