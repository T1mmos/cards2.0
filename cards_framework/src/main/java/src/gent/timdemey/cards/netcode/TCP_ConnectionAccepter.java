package gent.timdemey.cards.netcode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import gent.timdemey.cards.logging.Logger;

public final class TCP_ConnectionAccepter
{
    private final TCP_ConnectionPool connectionPool;
    private final int tcpport;
    private Thread thread = null;
    private ServerSocket ssocket = null;

    /**
     * Accepts incoming connections and once established, adds them to the given
     * connection pool.
     * 
     * @param connPool
     * @param info
     */
    public TCP_ConnectionAccepter(TCP_ConnectionPool connPool, int tcpport)
    {
        this.connectionPool = connPool;
        this.tcpport = tcpport;
    }

    public void start()
    {
        try
        {
            this.ssocket = new ServerSocket(tcpport);
            Logger.info("Created TCP server socket on port " + tcpport);
        }
        catch (IOException e)
        {
            Logger.error(e);
            return;
        }
        this.thread = new Thread(this::acceptLoop, "Server :: TCP ServerSocket Accepter");
        this.thread.start();
    }

    public void stop()
    {
        if (this.ssocket != null)
        {
            try
            {
                this.ssocket.close();
            }
            catch (IOException e)
            {
                Logger.error(e);
            }
        }
        if (this.thread != null)
        {
            this.thread.interrupt();
            this.thread = null;
        }
    }

    private void acceptLoop()
    {
        try
        {
            while (true)
            {
                Socket socket = ssocket.accept();
                connectionPool.addConnection(socket);                
            }
        }
        catch (SocketException ex)
        {
            // done listening, not an error
            Logger.info("Server socket closed");
        }
        catch (IOException e)
        {
            Logger.error(e);
        }
    }
}
