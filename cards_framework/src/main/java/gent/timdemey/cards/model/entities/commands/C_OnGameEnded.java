package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;

public class C_OnGameEnded extends CommandBase
{
    public final UUID winnerId;

    C_OnGameEnded(IContextService contextService, UUID id, UUID winnerId)
    {
        super(contextService, id);
        
        this.winnerId = winnerId;
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
