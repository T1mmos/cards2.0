package gent.timdemey.cards.services.context;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.netcode.TCP_Connection;
import gent.timdemey.cards.netcode.TCP_ConnectionPool;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.interfaces.INetworkService;
import gent.timdemey.cards.services.interfaces.ISerializationService;

public class CommandNetworkService implements INetworkService
{
    private final ISerializationService _SerializationService;
    private Logger _Logger;
    
    public CommandNetworkService (
        ISerializationService serializationService,
        Logger logger)
    {
        this._SerializationService = serializationService;
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
        String serialized = serialize(command);
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
            String serialized = serialize(command);
            tcpConnPool.broadcast(dstIds, serialized);
        }
        else
        {
            _Logger.debug("The command %s is not a local command, not so sending it", command.getName());
        }
    }
    
    private String serialize(CommandBase command)
    {
        CommandDtoMapper dtoMapper = _SerializationService.getCommandDtoMapper();
        String serialized = dtoMapper.toJson(command);
        return serialized;
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
