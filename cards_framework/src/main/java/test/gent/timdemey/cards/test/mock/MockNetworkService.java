package gent.timdemey.cards.test.mock;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.netcode.TCP_ConnectionPool;
import gent.timdemey.cards.services.INetworkService;

public class MockNetworkService implements INetworkService
{

    @Override
    public void send(UUID localId, UUID destination, CommandBase command, TCP_ConnectionPool tcpConnPool)
    {
        ILogManager logger = Services.get(ILogManager.class);
        logger.log("MockNetworkService: send()");
    }

    @Override
    public void broadcast(UUID localId, List<UUID> destinations, CommandBase command, TCP_ConnectionPool tcpConnPool)
    {
        ILogManager logger = Services.get(ILogManager.class);
        logger.log("MockNetworkService: broadcast()");
    }
}
