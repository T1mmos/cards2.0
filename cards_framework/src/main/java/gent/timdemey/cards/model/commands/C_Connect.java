package gent.timdemey.cards.model.commands;

import java.net.InetAddress;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionCreator;
import gent.timdemey.cards.multiplayer.io.TCP_ConnectionPool;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_Connect extends CommandBase
{    
    final InetAddress srvInetAddress;
    final int tcpport;
    final String playerName;
        
    C_Connect (InetAddress srvInetAddress, int tcpport, String playerName)
    {
        this.srvInetAddress = srvInetAddress;
        this.tcpport = tcpport;
        this.playerName = playerName;
    }
    
    @Override
    public void execute(Context context, ContextType type, State state) {        
        if (type == ContextType.UI)
        {
            state.setLocalName(playerName);
            
            reschedule(ContextType.Client);
        }
        else if (type == ContextType.Client)
        {            
        	state.setLocalName(playerName);
            ClientCommandProcessor cProcessor = getProcessorClient();
                        
            cProcessor.connPool = new TCP_ConnectionPool(1, new ClientCommandProcessor.ConnListener());
            TCP_ConnectionCreator creator = new TCP_ConnectionCreator(cProcessor.connPool);
            creator.connect(srvInetAddress, tcpport);
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void undo(Context context, ContextType type, State state)
    {
        
    }
}
