package gent.timdemey.cards.model.entities.commands;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.utils.Debug;

public class C_SetVisible extends CommandBase
{
    private final List<Card> cards;
    private final boolean visible;

    public C_SetVisible(List<Card> cards, boolean visible)
    {
        Preconditions.checkNotNull(cards);
        for (Card card : cards)
        {
            Preconditions.checkNotNull(card);
            Preconditions.checkArgument(card.visibleRef.get() != visible);
        }

        this.cards = new ArrayList<>(cards);
        this.visible = visible;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();
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

    @Override
    public boolean isSyncable()
    {
        return true;
    }
    
    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("visible", visible) + 
               Debug.listEntity("cards", cards);
    }
}
