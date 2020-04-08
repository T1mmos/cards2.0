package gent.timdemey.cards.multiplayer.io;

import java.util.UUID;

public interface ITcpConnectionListener
{
    public void onTcpMessageReceived(UUID id, TCP_Connection connection, String message);

    public void onTcpConnectionAdded(TCP_Connection connection, TCP_ConnectionPool connectionPool);

    public void onTcpConnectionLocallyClosed(UUID id, TCP_Connection connection);

    public void onTcpConnectionRemotelyClosed(UUID id, TCP_Connection connection);
}
