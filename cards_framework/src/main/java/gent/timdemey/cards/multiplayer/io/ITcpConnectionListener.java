package gent.timdemey.cards.multiplayer.io;

import java.util.UUID;

public interface ITcpConnectionListener
{
    public void onTcpMessageReceived (TCP_Connection connection, String message);
    public void onTcpConnectionAdded (TCP_Connection connection);
    public void onTcpConnectionLocallyClosed  (TCP_Connection connection, UUID id);
    public void onTcpConnectionRemotelyClosed (TCP_Connection connection, UUID id);
}
