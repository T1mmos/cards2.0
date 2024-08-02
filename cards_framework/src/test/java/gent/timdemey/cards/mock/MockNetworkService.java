package gent.timdemey.cards.mock;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.model.net.TCP_ConnectionPool;
import gent.timdemey.cards.services.interfaces.INetworkService;

public class MockNetworkService implements INetworkService
{

    @Override
    public void send(UUID localId, UUID destination, CommandBase command, TCP_ConnectionPool tcpConnPool)
    {        
        
    }

    @Override
    public void send(TCP_Connection conn, CommandBase command)
    {
       
    }
    
    @Override
    public void broadcast(UUID localId, List<UUID> destinations, CommandBase command, TCP_ConnectionPool tcpConnPool)
    {       
        
    }

}
