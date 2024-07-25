package gent.timdemey.cards.netcode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import gent.timdemey.cards.logging.Logger;

public final class TCP_ConnectionPool
{
    private final String name;
    /// connections not yet associated to a UUID (should be empty 99% of the time)
    private final List<TCP_Connection> halfConns;
    /// fully established connections that are associated to a UUID.
    private final ConcurrentMap<UUID, TCP_Connection> uuid2conn;
    private final List<UUID> locallyClosing;
    private final ITcpConnectionListener externalConnListener;
    private final int maxConnections;
    private final ExecutorService execServ;
    private Logger _Logger;

    public TCP_ConnectionPool(String name, int maxConnections, ITcpConnectionListener connListener, Logger logger)
    {
        if (connListener == null)
        {
            throw new IllegalArgumentException("You cannot use a TCP_ConnectionPool if you do not specify a callback to be invoked upon new incoming connections");
        }
              
        this.name = name;
        this.halfConns = Collections.synchronizedList(new ArrayList<>());
        this.uuid2conn = new ConcurrentHashMap<>();
        this.externalConnListener = connListener;
        this.maxConnections = maxConnections;
        this.locallyClosing = Collections.synchronizedList(new ArrayList<>());
        this.execServ = Executors.newSingleThreadExecutor(new ThreadFactory()
        {

            @Override
            public Thread newThread(Runnable r)
            {
                Thread thr = new Thread(r, name + " :: TCP_ConnectionPool");
                thr.setDaemon(true);
                return thr;
            }
        });
        this._Logger = logger;
    }

    void onTcpConnectionStarted(TCP_Connection connection)
    {
        externalConnListener.onTcpConnectionAdded(connection);
    }

    void onTcpMessageReceived(TCP_Connection connection, String str_in)
    {
        UUID id = findId(connection);
        externalConnListener.onTcpMessageReceived(id, connection, str_in);
    }

    void onTcpConnectionEnded(TCP_Connection connection)
    {
        if (halfConns.contains(connection))
        {
            halfConns.remove(connection);
        }
        else
        {
            UUID id = findId(connection);
            boolean local = false;

            if (id != null)
            {
                local = locallyClosing.remove(id);
                uuid2conn.remove(id);
                halfConns.remove(connection);
            }

            externalConnListener.onTcpConnectionClosed(id, connection, local);
        }
    }

    UUID findId(TCP_Connection connection)
    {
        UUID idToFind = null;
        if (uuid2conn.containsValue(connection))
        {
            for (UUID id : uuid2conn.keySet())
            {
                TCP_Connection value = uuid2conn.get(id);
                if (value == connection)
                {
                    idToFind = id;
                }
            }
        }
        return idToFind;
    }

    int getMaxConnections()
    {
        return maxConnections;
    }

    void addConnection(Socket socket)
    {
        if (socket == null)
        {
            throw new IllegalArgumentException("socket");
        }

        execServ.submit(() ->
        {
            addConnectionAndStart(socket);
        });
    }

    public void addConnection(InetAddress address, int port)
    {
        if (address == null)
        {
            throw new NullPointerException("address");
        }

        execServ.submit(() ->
        {
            Socket socket;
            try
            {
                String hostAddr = address.getHostAddress();
                _Logger.info("Connecting to %s:%s...", hostAddr, port);
                socket = new Socket(address, port);
                String localAddr = socket.getLocalAddress().getHostAddress();
                int localPort = socket.getLocalPort();
                _Logger.info("Connected to %s:%s, local address is %s:%s", hostAddr, port, localAddr, localPort);
                addConnectionAndStart(socket);
            }
            catch (IOException e)
            {
                _Logger.error(e);
            }
        });
    }

    private void addConnectionAndStart(Socket socket)
    {
        TCP_Connection connection = new TCP_Connection(name, socket, this, _Logger);
        halfConns.add(connection);
        connection.start();
    }

    /**
     * Only after the connection has been established, a client can send its UUID.
     * This is how to bind a UUID to a connection.
     * 
     * @param remote
     * @param inetAddress
     */
    public void bindUUID(UUID remote, TCP_Connection connection)
    {
        if (!halfConns.contains(connection))
        {
            throw new IllegalStateException(
                    "The given connection is not found in the pool, so it cannot be bound to a UUID");
        }

        TCP_Connection prev = uuid2conn.putIfAbsent(remote, connection);
        if (prev != null)
        {
            throw new IllegalStateException("Connection already bound to UUID: " + remote);
        }
        halfConns.remove(connection);
    }

    public TCP_Connection getConnection(UUID remote)
    {
        if (remote == null)
        {
            throw new IllegalArgumentException("remote");
        }
        
        TCP_Connection conn = uuid2conn.get(remote);
        if (conn == null)
        {
            throw new IllegalStateException("No connection in the pool for UUID: " + remote);
        }

        return conn;
    }

    public void broadcast(List<UUID> ids, String msg)
    {
        ids.stream().map(id -> getConnection(id)).forEach(conn -> conn.send(msg));
    }

    public void closeAllConnections()
    {
        for (UUID id : uuid2conn.keySet())
        {
            closeConnection(id);
        }
    }
    
    public void stop()
    {
        execServ.shutdown();
    }

    public void closeConnection(UUID remote)
    {
        if (remote == null)
        {
            throw new IllegalArgumentException("remote");
        }

        execServ.submit(() ->
        {
            TCP_Connection conn = uuid2conn.get(remote);
            if (conn == null)
            {
                throw new IllegalStateException("No connection in the pool for UUID: " + remote);
            }

            TCP_Connection tcpConnection = uuid2conn.get(remote);
            locallyClosing.add(remote);
            tcpConnection.stop();
        });
    }

    public int getHalfConnections()
    {
        return halfConns.size();
    }
}
