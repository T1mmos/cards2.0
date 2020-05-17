package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

public abstract class C_Use extends CommandBase
{
    protected final UUID initiatorStackId;
    protected final UUID initiatorCardId;

    public C_Use(UUID initiatorStackId, UUID initiatorCardId)
    {
        if ((initiatorStackId == null && initiatorCardId == null)
                || (initiatorStackId != null && initiatorCardId != null))
        {
            throw new IllegalArgumentException(
                    "Choose exactly one initator for a Use command: a card, or a card stack, but not both.");
        }
        this.initiatorStackId = initiatorStackId;
        this.initiatorCardId = initiatorCardId;
    }

    @Override
    protected final CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        if (state.getGameState() != GameState.Started)
        {
            return CanExecuteResponse.no("GameState should be Started but is: " + state.getGameState());
        }

        CommandBase cmd = resolveCommand(context, type, state);
        if (cmd == null)
        {
            return CanExecuteResponse.no("No command could be resolved");
        }

        return CanExecuteResponse.yes();
    }

    protected final void preExecute(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);

        CommandBase cmd = resolveCommand(context, type, state);
        schedule(ContextType.UI, cmd);
    };

    /**
     * Resolves the actual command to execute whenever a user invokes a 'Use'
     * action.
     * 
     * @param context
     * @param type
     * @param state
     * @return
     */
    protected abstract CommandBase resolveCommand(Context context, ContextType type, State state);

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("initiatorStackId", initiatorStackId)
                + Debug.getKeyValue("initiatorCardId", initiatorCardId);
    }
}
