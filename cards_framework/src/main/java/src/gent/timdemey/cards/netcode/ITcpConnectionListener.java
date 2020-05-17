package gent.timdemey.cards.netcode;

import java.util.UUID;

public interface ITcpConnectionListener
{
    public void onTcpMessageReceived(UUID id, TCP_Connection connection, String message);

    public void onTcpConnectionAdded(TCP_Connection connection);

    /**
     * Event triggered when a connection that was fully established has completely shut down.
     * @param id identifier of the connection
     * @param connection the connection itself 
     * @param local true if the closure was locally requested, false if the remote party 
     * closed the connection
     */
    public void onTcpConnectionClosed(UUID id, TCP_Connection connection, boolean local);
}
