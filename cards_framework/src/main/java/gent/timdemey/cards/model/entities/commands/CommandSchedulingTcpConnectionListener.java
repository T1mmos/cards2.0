package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;


import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.netcode.ITcpConnectionListener;
import gent.timdemey.cards.netcode.TCP_Connection;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.context.LimitedContext;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.ISerializationService;

public final class CommandSchedulingTcpConnectionListener implements ITcpConnectionListener
{
    private final ContextType contextType;

    public CommandSchedulingTcpConnectionListener(ContextType contextType)
    {
        this.contextType = contextType;
    }

    @Override
    public void onTcpConnectionAdded(TCP_Connection connection)
    {
        Logger.info("A TCP connection was added to " + connection.getRemote());
        
        C_OnTcpConnectionAdded cmd = new C_OnTcpConnectionAdded(connection);
        
        LimitedContext context = Services.get(IContextService.class).getContext(contextType);
        context.schedule(cmd);
    }

    @Override
    public void onTcpMessageReceived(UUID id, TCP_Connection tcpConnection, String message)
    {
        try
        {
            ISerializationService serServ = Services.get(ISerializationService.class);
            CommandBase command = serServ.getCommandDtoMapper().toCommand(message);

            // attach metadata to the command
            command.setSourceTcpConnection(tcpConnection);
            command.setSourceId(id);
            command.setSerialized(message);

            Logger.info("Received command '%s' from '%s'", command.getName(), tcpConnection.getRemote());

            LimitedContext context = Services.get(IContextService.class).getContext(contextType);
            context.schedule(command);
        }
        catch (Exception e)
        {
            Services.get(ILogManager.class).log(e);
        }
    }

    @Override
    public void onTcpConnectionClosed(UUID id, TCP_Connection connection, boolean local)
    {
        Logger.info("TCP connection closed by the %s party (id=%s)", local ? "local" : "remote", id);
     
        C_OnTcpConnectionClosed cmd = new C_OnTcpConnectionClosed(id, local);
        LimitedContext context = Services.get(IContextService.class).getContext(contextType);
        context.schedule(cmd);        
    }
}
