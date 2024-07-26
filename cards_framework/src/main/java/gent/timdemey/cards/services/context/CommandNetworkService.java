package gent.timdemey.cards.services.context;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.model.net.TCP_ConnectionPool;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.interfaces.INetworkService;

public class CommandNetworkService implements INetworkService
{
    private final CommandDtoMapper _CommandDtoMapper;
    private Logger _Logger;
    
    public CommandNetworkService (
        CommandDtoMapper commandDtoMapper,
        Logger logger)
    {
        this._CommandDtoMapper = commandDtoMapper;
        this._Logger = logger;
    }
    
    @Override
    public void send(UUID localId, UUID dstId, CommandBase command, TCP_ConnectionPool tcpConnPool)
    {
        List<UUID> dstIds = Arrays.asList(dstId);
        CheckArgs(localId, dstIds, command);
        sendPriv(localId, dstIds, command, tcpConnPool);
    }
    
    @Override 
    public void send(TCP_Connection conn, CommandBase command)
    {
        String serialized = _CommandDtoMapper.toJson(command);
        conn.send(serialized);
    }

    @Override
    public void broadcast(UUID localId, List<UUID> dstIds, CommandBase command, TCP_ConnectionPool tcpConnPool)
    {
        CheckArgs(localId, dstIds, command);
        sendPriv(localId, dstIds, command, tcpConnPool);
    }

    private void sendPriv(UUID localId, List<UUID> dstIds, CommandBase command, TCP_ConnectionPool tcpConnPool)
    {
        UUID srcId = command.getSourceId();

        // only send a command if the SourceId == LocalId, meaning the command
        // didn't originate from somewhere else.
        if (srcId.equals(localId))
        {
            String serialized = _CommandDtoMapper.toJson(command);
            tcpConnPool.broadcast(dstIds, serialized);
        }
        else
        {
            _Logger.debug("The command %s is not a local command, not so sending it", command.getName());
        }
    }
    
    private void CheckArgs(UUID localId, List<UUID> dstIds, CommandBase command)
    {
        if (localId == null)
        {
            throw new NullPointerException("localId");
        }
        if (dstIds == null)
        {
            throw new NullPointerException("destination");
        }
        if (command == null)
        {
            throw new NullPointerException("command");
        }

        if (command.getSourceId() == null)
        {
            command.setSourceId(localId);
        }
    }
}
