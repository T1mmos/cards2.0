package gent.timdemey.cards.netcode;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import gent.timdemey.cards.logging.Logger;

public final class TCP_ConnectionAccepter
{
    private final TCP_ConnectionPool connectionPool;
    private final int tcpport;
    private final String messageOnFull;

    private final Executor rejectedExecutor;

    private Thread thread = null;
    private ServerSocket ssocket = null;

    /**
     * Accepts incoming connections and once established, adds them to the given
     * connection pool.
     * 
     * @param connPool
     * @param info
     */
    public TCP_ConnectionAccepter(TCP_ConnectionPool connPool, int tcpport, String messageOnFull)
    {
        this.connectionPool = connPool;
        this.tcpport = tcpport;
        this.messageOnFull = messageOnFull;
        this.rejectedExecutor = Executors.newCachedThreadPool(new ThreadFactory()
        {

            @Override
            public Thread newThread(Runnable r)
            {
                return new Thread(r, "Rejected Connection Handler");
            }
        });
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (this.thread != null)
        {
            this.thread.interrupt();
            this.thread = null;
        }
    }

    private class RejectedConnectionTask implements Runnable
    {

        private final Socket socket;

        private RejectedConnectionTask(Socket socket)
        {
            this.socket = socket;
        }

        @Override
        public void run()
        {
            TCP_Connection conn = new TCP_Connection("REJECTED", socket, null);
            conn.send(messageOnFull);

            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            conn.stop();
        }

    }

    private void acceptLoop()
    {
        try
        {
            while (true)
            {
                Socket socket = ssocket.accept();
                if (connectionPool.isFull())
                {
                    rejectedExecutor.execute(new RejectedConnectionTask(socket));
                }
                else
                {
                    connectionPool.addConnection(socket);
                }
            }
        }
        catch (SocketException ex)
        {
            // done listening
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
