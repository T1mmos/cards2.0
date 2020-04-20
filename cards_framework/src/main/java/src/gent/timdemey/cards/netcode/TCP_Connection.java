package gent.timdemey.cards.netcode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;

public final class TCP_Connection
{

    private final Socket socket;
    private final Thread thread;
    private final TCP_ConnectionPool pool;

    TCP_Connection(Socket socket, TCP_ConnectionPool pool)
    {
        this.socket = socket;
        this.thread = new Thread(() -> listen(), "TCP Connection to " + getRemote());
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
        this.thread.start();
    }

    void stop()
    {
        try
        {
            this.socket.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.thread.interrupt();
    }

    private void listen()
    {
        pool.onTcpConnectionStarted(this);
        try
        {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), IoConstants.TCP_CHARSET));

            while (true)
            {
                String str_in = reader.readLine();
                if (str_in == null)
                {
                    throw new IOException("read a null from the socket stream, assuming the remote socket was closed");
                }
                pool.onTcpMessageReceived(this, str_in);
            }
        }
        catch (IOException e)
        {
            pool.onTcpConnectionEnded(this);
            Services.get(ILogManager.class).log("Following exception may be expected (connection closing):");
            Services.get(ILogManager.class).log(e);
        }
    }

    public void send(String s)
    {
        try
        {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream(), IoConstants.TCP_CHARSET));
            writer.write(s);
            writer.newLine();
            writer.flush();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
