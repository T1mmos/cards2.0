package gent.timdemey.cards.model.entities.commands;

import java.util.UUID;

import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public abstract class C_Use extends CommandBase
{
    protected final UUID initiatorStackId;
    protected final UUID initiatorCardId;

    public C_Use(UUID initiatorStackId, UUID initiatorCardId)
    {
        if((initiatorStackId == null && initiatorCardId == null) || (initiatorStackId != null && initiatorCardId != null))
        {
            throw new IllegalArgumentException("Choose exactly one initator for a Use command: a card, or a card stack, but not both.");
        }
        this.initiatorStackId = initiatorStackId;
        this.initiatorCardId = initiatorCardId;
    }

    @Override
    protected final boolean canExecute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Client, ContextType.Server);

        CommandBase cmd = resolveCommand(context, type, state);
        return cmd != null;
    }

    protected final void execute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Client, ContextType.Server);

        CommandBase cmd = resolveCommand(context, type, state);
        schedule(ContextType.UI, cmd);
    };
    
    /**
     * Resolves the actual command to execute whenever a user invokes a 'Use' action.
     * @param context
     * @param type
     * @param state
     * @return
     */
    protected abstract CommandBase resolveCommand(Context context, ContextType type, State state);
}
