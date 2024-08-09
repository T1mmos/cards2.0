package gent.timdemey.cards.model.entities.commands.game;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

public abstract class C_Use extends CommandBase<P_Use>
{
    protected final UUID initiatorStackId;
    protected final UUID initiatorCardId;

    public C_Use(
        Container container,
        P_Use parameters)
    {
        super(container, parameters);
        
        if ((parameters.initiatorStackId == null && parameters.initiatorCardId == null)
                || (parameters.initiatorStackId != null && parameters.initiatorCardId != null))
        {
            throw new IllegalArgumentException(
                    "Choose exactly one initator for a Use command: a card, or a card stack, but not both.");
        }
        this.initiatorStackId = parameters.initiatorStackId;
        this.initiatorCardId = parameters.initiatorCardId;
    }

    @Override
    public final CanExecuteResponse canExecute()
    {
        CheckContext(ContextType.UI);
        if (_State.getGameState() != GameState.Started)
        {
            return CanExecuteResponse.no("GameState should be Started but is: " + _State.getGameState());
        }

        CommandBase cmd = resolveCommand();
        if (cmd == null)
        {
            return CanExecuteResponse.no("No command could be resolved");
        }

        return CanExecuteResponse.yes();
    }

    @Override
    public final void execute()
    {
        CheckContext(ContextType.UI);

        CommandBase cmd = resolveCommand();
        schedule(ContextType.UI, cmd);
    };

    /**
     * Resolves the actual command to execute whenever a user invokes a 'Use'
     * action.
     * 
     * @return
     */
    protected abstract CommandBase resolveCommand();

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("initiatorStackId", initiatorStackId)
                + Debug.getKeyValue("initiatorCardId", initiatorCardId);
    }
}
