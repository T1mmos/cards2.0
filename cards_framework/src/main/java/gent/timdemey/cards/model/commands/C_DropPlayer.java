package gent.timdemey.cards.model.commands;

import java.util.UUID;

import gent.timdemey.cards.model.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_DropPlayer extends CommandBase {

    private final UUID playerId;
    
    C_DropPlayer(UUID playerId) 
    {
        this.playerId = playerId;
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }
    
    @Override
    public void execute(Context context, ContextType contextType, State state) 
    {
    	for (Player player : state.getPlayers())
    	{
    		if (player.id.equals(playerId))
    		{
    			state.removePlayer(player);         
    		}
    	}
        
        if (contextType == ContextType.Client)
        {
            reschedule(ContextType.UI);
        }
        else if (contextType == ContextType.Server)
        {
            String json = CommandDtoMapper.toJson(this);
            for (Player player : state.getRemotePlayers())
        	{
        		state.getTcpConnectionPool().getConnection(player.id).send(json);
        	}
        }
    }

}
