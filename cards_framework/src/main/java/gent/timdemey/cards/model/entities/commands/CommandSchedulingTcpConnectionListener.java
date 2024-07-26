package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;


import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.net.ITcpConnectionListener;
import gent.timdemey.cards.model.net.NetworkFactory;
import gent.timdemey.cards.model.net.TCP_Connection;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;
import gent.timdemey.cards.services.interfaces.IContextService;

public final class CommandSchedulingTcpConnectionListener implements ITcpConnectionListener
{
    private final ContextType contextType;
    private final Logger _Logger;
    private final NetworkFactory _NetworkFactory;
    private final IContextService _ContextService;
    private final CommandDtoMapper _CommandDtoMapper;
    private final CommandFactory _CommandFactory;

    public CommandSchedulingTcpConnectionListener(
        IContextService contextService, 
        CommandDtoMapper commandDtoMapper,
        CommandFactory commandFactory,
        NetworkFactory networkFactory,
        Logger logger,
        ContextType contextType)
    {
        this._ContextService = contextService;
        this._CommandDtoMapper = commandDtoMapper;
        this._CommandFactory = commandFactory;
        this._NetworkFactory = networkFactory;
        this._Logger = logger;
        
        this.contextType = contextType;
    }

    @Override
    public void onTcpConnectionAdded(TCP_Connection connection)
    {
        _Logger.info("A TCP connection was added to " + connection.getRemote());
        
        C_OnTcpConnectionAdded cmd = _NetworkFactory.CreateOnTcpConnectionAdded(connection);
        
        LimitedContext context = _ContextService.getContext(contextType);
        context.schedule(cmd);
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

            _Logger.info("Received command '%s' from '%s'", command.getName(), tcpConnection.getRemote());

            LimitedContext context = _ContextService.getContext(contextType);
            context.schedule(command);
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
        LimitedContext context = _ContextService.getContext(contextType);
        context.schedule(cmd);        
    }
}
