package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.payload.P_OnEndGame;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_OnGameEnded extends CommandBase
{
    public final UUID winnerId;

    public C_OnGameEnded(UUID winnerId)
    {
        this.winnerId = winnerId;
    }

    public C_OnGameEnded(P_OnEndGame pl)
    {
        super(pl);
        this.winnerId = pl.winnerId;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Server);
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);

        state.setGameState(GameState.Ended);
        D_OnEndGame d_onendgame = new D_OnEndGame(winnerId);
        schedule(ContextType.UI, d_onendgame);
    }
}
