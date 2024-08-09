package gent.timdemey.cards.model.entities.commands.net;

import java.util.UUID;

import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.commands.CommandFactory;
import gent.timdemey.cards.model.net.ITcpConnectionListener;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.context.ICommandExecutor;

public final class CommandSchedulingTcpConnectionListener implements ITcpConnectionListener
{
    private final Logger _Logger;
    private final CommandDtoMapper _CommandDtoMapper;
    private final CommandFactory _CommandFactory;
    private final ICommandExecutor _CommandExecutor;

    public CommandSchedulingTcpConnectionListener(
        CommandDtoMapper commandDtoMapper,
        CommandFactory commandFactory,
        Logger logger,
        ICommandExecutor commandExecutor)
    {
        this._CommandDtoMapper = commandDtoMapper;
        this._CommandFactory = commandFactory;
        this._Logger = logger;
        this._CommandExecutor = commandExecutor;
    }

    @Override
    public void onTcpConnectionAdded(TCP_Connection connection)
    {
        _Logger.info("A TCP connection was added to " + connection.getRemote());
        
        C_TCP_HandleNew cmd = _CommandFactory.CreateTCPHandleNew(connection);        
        _CommandExecutor.schedule(cmd);
    }

    @Override
    public void onTcpMessageReceived(UUID id, TCP_Connection tcpConnection, String message)
    {
        try
        {
            CommandBase command = _CommandDtoMapper.toCommand(message);

            // attach metadata to the command
            command.setSourceTcpConnection(tcpConnection);

            _Logger.info("Received command '%s' (id='%s', ContextType='%s') from '%s'", command.getName(), command.id, command.creatorContextType, tcpConnection.getRemote());

            _CommandExecutor.schedule(command);
        }
        catch (Exception e)
        {
            _Logger.error(e);
        }
    }

    @Override
    public void onTcpConnectionClosed(UUID id, TCP_Connection connection, boolean local)
    {
        _Logger.info("TCP connection closed by the %s party (id=%s)", local ? "local" : "remote", id);
     
        C_TCP_HandleClosed cmd = _CommandFactory.CreateTCPHandleClosed(id, local);        
        _CommandExecutor.schedule(cmd);        
    }
}
