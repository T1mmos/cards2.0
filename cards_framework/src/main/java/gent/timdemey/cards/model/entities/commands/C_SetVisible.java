package gent.timdemey.cards.model.entities.commands;

import java.util.ArrayList;
import java.util.List;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.utils.Debug;
import java.util.UUID;

public class C_SetVisible extends CommandBase
{
    private final List<Card> cards;
    private final boolean visible;

    C_SetVisible(
        IContextService contextService, 
        UUID id, List<Card> cards, boolean visible)
    {
        super(contextService, id);
        
        if (cards == null)
        {
            throw new IllegalArgumentException("cards");
        }
        for (int i = 0; i < cards.size(); i++)
        {
            Card card = cards.get(i);
            if (card == null)
            {
                throw new IllegalArgumentException("cards[" + i + "]");
            }
            if (card.visibleRef.get() == visible)
            {
                throw new IllegalArgumentException("cards[" + i + "] visibility is already set to " + visible);
            }
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
    public CommandType getCommandType()
    {
        return CommandType.DEFAULT;
    }
    
    @Override
    public String toDebugString()
    {
        return Debug.getKeyValue("visible", visible) + 
               Debug.listEntity("cards", cards);
    }
}
