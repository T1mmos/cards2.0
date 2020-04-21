package gent.timdemey.cards.test.mock;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.netcode.TCP_ConnectionPool;
import gent.timdemey.cards.services.INetworkService;

public class MockNetworkService implements INetworkService
{

    @Override
    public void send(UUID localId, UUID destination, CommandBase command, TCP_ConnectionPool tcpConnPool)
    {
        // do nothing
    }

    @Override
    public void broadcast(UUID localId, List<UUID> destinations, CommandBase command, TCP_ConnectionPool tcpConnPool)
    {
        // do nothing
    }
}
