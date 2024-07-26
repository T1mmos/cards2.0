package gent.timdemey.cards.services.interfaces;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.model.net.TCP_ConnectionPool;

public interface INetworkService
{
    void send(UUID localId, UUID destination, CommandBase command, TCP_ConnectionPool tcpConnPool);
    void send(TCP_Connection conn, CommandBase command);
    void broadcast(UUID localId, List<UUID> destinations, CommandBase command, TCP_ConnectionPool tcpConnPool);
}
