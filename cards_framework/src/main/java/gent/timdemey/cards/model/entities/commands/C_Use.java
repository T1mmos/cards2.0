package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Use;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.utils.Debug;

public abstract class C_Use extends CommandBase
{
    protected final UUID initiatorStackId;
    protected final UUID initiatorCardId;

    public C_Use(
        IContextService contextService, State state,
        P_Use parameters)
    {
        super(contextService, state, parameters);
        
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
    protected final CanExecuteResponse canExecute(Context context, ContextType type)
    {
        CheckContext(type, ContextType.UI);
        if (_State.getGameState() != GameState.Started)
        {
            return CanExecuteResponse.no("GameState should be Started but is: " + _State.getGameState());
        }

        CommandBase cmd = resolveCommand(context, type);
        if (cmd == null)
        {
            return CanExecuteResponse.no("No command could be resolved");
        }

        return CanExecuteResponse.yes();
    }

    protected final void execute(Context context, ContextType type)
    {
        CheckContext(type, ContextType.UI);

        CommandBase cmd = resolveCommand(context, type);
        schedule(ContextType.UI, cmd);
    };

    /**
     * Resolves the actual command to execute whenever a user invokes a 'Use'
     * action.
     * 
     * @param context
     * @param type
     * @return
     */
    protected abstract CommandBase resolveCommand(Context context, ContextType type);

    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("initiatorStackId", initiatorStackId)
                + Debug.getKeyValue("initiatorCardId", initiatorCardId);
    }
}
