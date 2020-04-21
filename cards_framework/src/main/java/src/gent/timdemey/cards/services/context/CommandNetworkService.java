package gent.timdemey.cards.services.context;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.netcode.TCP_ConnectionPool;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.INetworkService;
import gent.timdemey.cards.services.ISerializationService;

public class CommandNetworkService implements INetworkService
{

    @Override
    public void send(UUID localId, UUID dstId, CommandBase command, TCP_ConnectionPool tcpConnPool)
    {
        List<UUID> dstIds = Arrays.asList(dstId);
        CheckArgs(localId, dstIds, command, tcpConnPool);
        sendPriv(localId, dstIds, command, tcpConnPool);
    }

    @Override
    public void broadcast(UUID localId, List<UUID> dstIds, CommandBase command, TCP_ConnectionPool tcpConnPool)
    {
        CheckArgs(localId, dstIds, command, tcpConnPool);
        sendPriv(localId, dstIds, command, tcpConnPool);
    }

    private void sendPriv(UUID localId, List<UUID> dstIds, CommandBase command, TCP_ConnectionPool tcpConnPool)
    {
        UUID srcId = command.getSourceId();

        // only send a command if the SourceId == LocalId, meaning the command
        // didn't originate from somewhere else.
        if (srcId.equals(localId))
        {
            CommandDtoMapper dtoMapper = Services.get(ISerializationService.class).getCommandDtoMapper();
            String serialized = dtoMapper.toJson(command);
            tcpConnPool.broadcast(dstIds, serialized);
        }
        else
        {
            ILogManager logger = Services.get(ILogManager.class);
            logger.log("The command %s is not a local command, not so sending it", command.getName());
        }
    }

    private void CheckArgs(UUID localId, List<UUID> dstIds, CommandBase command, TCP_ConnectionPool tcpConnPool)
    {
        if (localId == null)
        {
            throw new NullPointerException("localId");
        }
        if (dstIds == null)
        {
            throw new NullPointerException("destination");
        }
        if (dstIds.size() == 0)
        {
            throw new IllegalStateException("To send or broadcast, at least one DestinationId is expected");
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
