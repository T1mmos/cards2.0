package gent.timdemey.cards.netcode;

import java.util.UUID;

public interface ITcpConnectionListener
{
    public void onTcpMessageReceived(UUID id, TCP_Connection connection, String message);

    public void onTcpConnectionAdded(TCP_Connection connection);

    public void onTcpConnectionLocallyClosed(UUID id, TCP_Connection connection);

    public void onTcpConnectionRemotelyClosed(UUID id, TCP_Connection connection);
}
