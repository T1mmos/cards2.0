package gent.timdemey.cards.model.entities.commands.game;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.services.context.ContextType;

public class C_OnGameEnded extends CommandBase
{
    public final UUID winnerId;

    public C_OnGameEnded(
        Container container,
        P_OnGameEnded parameters)
    {
        super(container, parameters);
                
        this.winnerId = parameters.winnerId;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        CheckContext(ContextType.UI);
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        CheckContext(ContextType.UI);

        _State.setGameState(GameState.Ended);
    }
}
