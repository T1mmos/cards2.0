package gent.timdemey.cards.model.commands;

import java.util.UUID;

import gent.timdemey.cards.model.commands.payload.P_SolShowOnEndGame;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_SolShowOnEndGame extends CommandBase
{
    public final UUID winnerId;
    
    public C_SolShowOnEndGame(UUID winnerId)
    {
        this.winnerId = winnerId;
    }
    
    public C_SolShowOnEndGame(P_SolShowOnEndGame pl)
    {
        super(pl);
        this.winnerId = pl.winnerId;
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Server);
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Server);
        
        if (type == ContextType.Client)
        {
            forward(type, state);
            return;
        }
        
        state.setGameState(GameState.Ended);
        D_SolShowOnEndGame d_onendgame = new D_SolShowOnEndGame(winnerId);
        schedule(ContextType.UI, d_onendgame);
    }
}
