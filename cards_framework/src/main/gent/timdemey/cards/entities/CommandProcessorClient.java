package gent.timdemey.cards.entities;

import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.ILogManager;
import gent.timdemey.cards.multiplayer.io.IConnectionListener;
import gent.timdemey.cards.multiplayer.io.TCP_Connection;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.multiplayer.io.UDP_ServiceRequester;

/**
 * Asynchronously processes commands. This is where all commands, from UI
 * and from the server, are processed. It is also here where commands are
 * validated and where rollback actions are initiated if necessary.
 * <p>Adding commands will never block and is thread safe as well.
 * @author Timmos
 */
class CommandProcessorClient extends ACommandProcessorAsync {
    
    public static String THREAD_NAME = "CommandProcessorClient";
    
    UDP_ServiceRequester serviceRequester = null;
    TCP_ConnectionPool connPool = null;
    
    static class ConnListener implements IConnectionListener
    {

        @Override
        public void onTcpMessageReceived(TCP_Connection connection, String message) {
            try 
            {
                ICommand cmd = Json.receive(message).command;
                cmd.setVolatileData(connection);
                
                Services.get(ILogManager.class).log("Received command '" + cmd.getClass().getSimpleName() + "' from " + connection.getRemote());
                cmd.scheduleOn(ContextType.Client);
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
            ContextLimited context = Services.get(IContextProvider.class).getContext(ContextType.Client);            
            
            C_JoinGame cmd = new C_JoinGame(new MetaInfo(0,0,context.localPlayerId), context.localPlayerName, context.localPlayerId);
            cmd.setVolatileData(connection);
            cmd.scheduleOn(ContextType.Client);
        }

        @Override
        public void onTcpConnectionLocallyClosed(TCP_Connection connection, UUID id) 
        {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onTcpConnectionRemotelyClosed(TCP_Connection connection, UUID id) {
            ContextLimited context = Services.get(IContextProvider.class).getContext(ContextType.Client);     
            if (id != null)
            {
                if (id.equals(context.serverId))
                {
                    ICommand cmd = new C_ConnectionLost(new MetaInfo(0,0,context.localPlayerId));
                    cmd.scheduleOn(ContextType.Client);
                }
            }
        }
        
    }
    
    @Override
    protected String getThreadName() {
        return THREAD_NAME;
    }
        
    /**
     * Actually executes a command, which alters the game state, detects 
     * invalidity, sends updates to the server, ...
     * @param command
     */
    @Override
    protected void execute(ICommand command)
    {
        Services.get(ILogManager.class).log("Processing command '" + command.getClass().getSimpleName() +"'");
        Preconditions.checkState(Thread.currentThread().getName().equals(THREAD_NAME));
        
        command.execute();
        
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        UUID localId = context.getLocalId();
        if (command instanceof C_Move || command instanceof C_Composite)
        {
            if (localId.equals(command.getMetaInfo().requestingParty))
            {
                TCP_Connection connection = connPool.getConnection(context.getServerId());
                connection.send(command.serialize());
            }
            else 
            {
                command.scheduleOn(ContextType.UI);
            }
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
        if (!(env_in.command instanceof C_UDP_HelloClient))
        {
            Services.get(ILogManager.class).log("Unexpected command on UDP datagram, class: " + env_in.command.getClass().getSimpleName());
            return;
        }
        
        env_in.command.scheduleOn(ContextType.Client);
    }
}
