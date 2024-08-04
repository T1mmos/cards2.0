package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.net.ITcpConnectionListener;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.ICommandExecutor;

public final class CommandSchedulingTcpConnectionListener implements ITcpConnectionListener
{
    private final ContextType _ContextType;
    private final Logger _Logger;
    private final CommandDtoMapper _CommandDtoMapper;
    private final CommandFactory _CommandFactory;
    private final ICommandExecutor _CommandExecutor;

    public CommandSchedulingTcpConnectionListener(
        CommandDtoMapper commandDtoMapper,
        CommandFactory commandFactory,
        Logger logger,
        ContextType contextType,
        ICommandExecutor commandExecutor)
    {
        this._CommandDtoMapper = commandDtoMapper;
        this._CommandFactory = commandFactory;
        this._Logger = logger;
        this._ContextType = contextType;
        this._CommandExecutor = commandExecutor;
    }

    @Override
    public void onTcpConnectionAdded(TCP_Connection connection)
    {
        _Logger.info("A TCP connection was added to " + connection.getRemote());
        
        C_OnTcpConnectionAdded cmd = _CommandFactory.CreateOnTcpConnectionAdded(connection);        
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
            command.setSourceId(id);
            command.setSerialized(message);

            _Logger.info("Received command '%s' (id='%s') from '%s'", command.getName(), command.id, tcpConnection.getRemote());

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
     
        C_OnTcpConnectionClosed cmd = _CommandFactory.CreateOnTcpConnectionClosed(id, local);        
        _CommandExecutor.schedule(cmd);        
    }
}
