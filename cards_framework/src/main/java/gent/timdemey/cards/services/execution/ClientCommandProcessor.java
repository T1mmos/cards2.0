package gent.timdemey.cards.services.execution;

import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.contract.ICommand;
import gent.timdemey.cards.dto.Json;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.model.commands.C_Composite;
import gent.timdemey.cards.model.commands.C_HandleConnectionLoss;
import gent.timdemey.cards.model.commands.C_JoinGame;
import gent.timdemey.cards.model.commands.C_Move;
import gent.timdemey.cards.model.commands.CommandBase;
import gent.timdemey.cards.model.commands.CommandEnvelope;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.IConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceRequester;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.state.ContextLimited;

/**
 * Asynchronously processes commands. This is where all commands, from UI
 * and from the server, are processed. It is also here where commands are
 * validated and where rollback actions are initiated if necessary.
 * <p>Adding commands will never block and is thread safe as well.
 * @author Timmos
 */
class ClientCommandProcessor extends CommandProcessorBase {
    
    public static String THREAD_NAME = "CommandProcessorClient";
    
    UDP_ServiceRequester serviceRequester = null;
    TCP_ConnectionPool connPool = null;
    
    static class ConnListener implements IConnectionListener
    {

        @Override
        public void onTcpMessageReceived(TCP_Connection connection, String message) {
            try 
            {
                CommandEnvelope envelope = Json.receive(message);
                ICommand cmd = envelope.command;
                cmd.setVolatileData(connection);
                
                Services.get(ILogManager.class).log("Received command '" + cmd.getClass().getSimpleName() + "' from " + connection.getRemote());
                envelope.reschedule(ContextType.Client);
            } 
            catch (Exception e)
            {
                Services.get(ILogManager.class).log(e);
            }
        }

        @Override
        public void onTcpConnectionAdded(TCP_Connection connection)
        {
            Services.get(ILogManager.class).log("A TCP connection was added to " + connection.getRemote());
            ContextLimited context = Services.get(IContextService.class).getContext(ContextType.Client);            
            
            C_JoinGame cmd = new C_JoinGame(context.localPlayerName, context.localPlayerId);
            cmd.setVolatileData(connection);
            cmd.schedule(ContextType.Client);
        }

        @Override
        public void onTcpConnectionLocallyClosed(TCP_Connection connection, UUID id) 
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onTcpConnectionRemotelyClosed(TCP_Connection connection, UUID id) {
            ContextLimited context = Services.get(IContextService.class).getContext(ContextType.Client);     
            if (id != null)
            {
                if (id.equals(context.serverId))
                {
                    ICommand cmd = new C_HandleConnectionLoss();
                    Services.get(IContextService.class).getContext(ContextType.Client).commandProcessor.schedule(cmd);
                }
            }
        }
        
    }
            
    /**
     * Actually executes a command, which alters the game state, detects 
     * invalidity, sends updates to the server, ...
     * @param command
     */
    @Override
    protected void execute(CommandBase command, State state)
    {
        Services.get(ILogManager.class).log("Processing command '" + command.getClass().getSimpleName() +"'");
        Preconditions.checkState(Thread.currentThread().getName().equals(THREAD_NAME));
        
        if (command.canExecute(state))
        {
            command.execute(state);
        }
        else
        {
            // todo
        }        
    }

    /**
     * Called every time a server sends a CommandHelloClient response for a CommandHelloServer request.
     * @param command
     */
    void onUdpReceived(String msg)
    {
        // json to envelope
        CommandEnvelope env_in;
        try {
            env_in = Json.receive(msg);
        } catch (Exception e) {
            Services.get(ILogManager.class).log("Failed to deserialize UDP datagram, ignoring");
            return;
        }
        
        // UDP only accepts CommandHelloClient
        if (!(env_in.command instanceof HelloClientCommand))
        {
            Services.get(ILogManager.class).log("Unexpected command on UDP datagram, class: " + env_in.command.getClass().getSimpleName());
            return;
        }
        
        env_in.reschedule(ContextType.Client);
    }
}
