package gent.timdemey.cards.model.commands;

import java.util.List;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_SetVisible extends CommandBase
{
    private final List<Card> cards;
    private final boolean visible;

    C_SetVisible(List<Card> cards, boolean visible)
    {
        Preconditions.checkNotNull(cards);
        for (Card card : cards)
        {
            Preconditions.checkNotNull(card);
            Preconditions.checkArgument(card.visibleRef.get() != visible);
        }

        this.cards = cards;
        this.visible = visible;
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        for (Card card : cards)
        {
            card.visibleRef.set(visible);
        }
    }

    @Override
    protected boolean canUndo(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void undo(Context context, ContextType type, State state)
    {
        for (Card card : cards)
        {
            card.visibleRef.set(!visible);
        }
    }
}
