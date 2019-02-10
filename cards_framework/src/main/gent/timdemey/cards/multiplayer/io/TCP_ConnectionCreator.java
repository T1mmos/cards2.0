package gent.timdemey.cards.multiplayer.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;

public class TCP_ConnectionCreator 
{
    private final TCP_ConnectionPool connPool;
    
    public TCP_ConnectionCreator(TCP_ConnectionPool connPool)
    {
        Preconditions.checkNotNull(connPool);
        
        this.connPool = connPool;
    }
    
    public void connect (InetAddress address, int port)
    {
        Socket socket;
        try {
            socket = new Socket(address, port);
            connPool.addConnection(socket);
        } catch (IOException e) {
            Services.get(ILogManager.class).log(e);
        }
    }
}
