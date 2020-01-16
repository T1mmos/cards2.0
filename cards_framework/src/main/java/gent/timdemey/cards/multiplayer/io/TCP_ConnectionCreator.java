package gent.timdemey.cards.multiplayer.io;

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
            socket = new Socket(address, port);
            tcpConnPool.addConnection(socket);
        }
        catch (IOException e)
        {
            Services.get(ILogManager.class).log(e);
        }
    }
}
