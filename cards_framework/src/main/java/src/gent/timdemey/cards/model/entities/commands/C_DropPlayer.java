package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.logging.Logger;
import gent.timdemey.cards.model.entities.commands.payload.P_DropPlayer;
import gent.timdemey.cards.model.entities.game.Player;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.INetworkService;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

public class C_DropPlayer extends CommandBase
{
    public final UUID playerId;

    public C_DropPlayer(UUID playerId)
    {
        this.playerId = playerId;
    }

    public C_DropPlayer(P_DropPlayer pl)
    {
        super(pl);
        this.playerId = pl.playerId;
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    public void execute(Context context, ContextType contextType, State state)
    {
        Player player = state.getPlayers().remove(playerId);
        Logger.info("Removed player %s", player.name);
        
        if (contextType == ContextType.Server)
        {
            INetworkService ns = Services.get(INetworkService.class);            
            ns.broadcast(state.getLocalId(), state.getRemotePlayerIds(), this, state.getTcpConnectionPool());            
        }
    }

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("playerId", playerId);
    }

}
