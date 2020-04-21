package gent.timdemey.cards.netcode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingDeque;

import gent.timdemey.cards.logging.Logger;

public final class TCP_Connection
{
    private final Socket socket;
    private Thread thread_read;
    private Thread thread_send;
    private final LinkedBlockingDeque<String> queue_send;
    private final TCP_ConnectionPool pool;
    private boolean started = false;
    private boolean ended = false;

    TCP_Connection(Socket socket, TCP_ConnectionPool pool)
    {
        this.socket = socket;
        this.thread_read = new Thread(() -> read(), "TCP read (" + getRemote() + ")");
        this.thread_send = new Thread(() -> send(), "TCP send (" + getRemote() + ")");
        this.queue_send = new LinkedBlockingDeque<>();
        this.pool = pool;
    }

    public String getRemote()
    {
        InetSocketAddress isAddr = ((InetSocketAddress) socket.getRemoteSocketAddress());
        String ip = isAddr.getAddress().getHostAddress();
        int port = isAddr.getPort();
        String formatted = String.format("%s:%s", ip, port);
        return formatted;
    }

    void start()
    {
        synchronized (this)
        {
            if(started || ended)
            {
                return;
            }
            started = true;
        }
        
        pool.onTcpConnectionStarted(this);        
        thread_read.start();
        thread_send.start();
    }

    void stop()
    {
        synchronized (this)
        {
            if (!started || ended)
            {
                return;
            }
            ended = true;
        }

        if (!socket.isClosed())
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        if (thread_read != null)
        {
            thread_read.interrupt();
            thread_read = null;
        }
        if (thread_send != null)
        {
            queue_send.clear();
            
            thread_send.interrupt();
            thread_send = null;
        }

        pool.onTcpConnectionEnded(this);
    }

    private void onException(IOException e)
    {
        Logger.error("Following exception may be expected (connection closing):", e);
        stop();
    }

    private void read()
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), IoConstants.TCP_CHARSET));

            while (true)
            {
                String str_in = reader.readLine();
                if (str_in == null)
                {
                    throw new IOException("Read a poison pill value (null). All threads linked to this TCP socket will shut down.");
                }
                pool.onTcpMessageReceived(this, str_in);
            }
        }
        catch (IOException e)
        {
            onException(e);
        }
    }

    private void send()
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), IoConstants.TCP_CHARSET));
            
            while(true)
            {
                String str_out = queue_send.take();
                if (str_out == null)
                {
                    throw new IOException("Send thread received a poison pill value (null). All threads linked to this TCP socket will shut down.");
                }

                writer.write(str_out);
                writer.newLine();
                writer.flush();
            }
        }
        catch (IOException e)
        {
            onException(e);
        }
        catch (InterruptedException e2)
        {
            Thread.currentThread().interrupt();
        }
    }

    public void send(String str)
    {
        queue_send.add(str);
    }
}
