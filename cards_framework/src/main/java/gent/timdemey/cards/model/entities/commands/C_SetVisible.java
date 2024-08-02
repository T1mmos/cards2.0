package gent.timdemey.cards.model.entities.commands;

import java.util.ArrayList;
import java.util.List;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_SetVisible;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.utils.Debug;

public class C_SetVisible extends CommandBase
{
    private final List<Card> cards;
    private final boolean visible;

    public C_SetVisible(
        IContextService contextService, State state,
        P_SetVisible parameters)
    {
        super(contextService, state, parameters);
        
        if (parameters.cards == null)
        {
            throw new IllegalArgumentException("cards");
        }
        for (int i = 0; i < parameters.cards.size(); i++)
        {
            Card card = parameters.cards.get(i);
            if (card == null)
            {
                throw new IllegalArgumentException("cards[" + i + "]");
            }
            if (card.visibleRef.get() == parameters.visible)
            {
                throw new IllegalArgumentException("cards[" + i + "] visibility is already set to " + parameters.visible);
            }
        }

        this.cards = new ArrayList<>(parameters.cards);
        this.visible = parameters.visible;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void execute(Context context, ContextType type)
    {
        for (Card card : cards)
        {
            card.visibleRef.set(visible);
        }
    }

    @Override
    protected boolean canUndo(Context context, ContextType type)
    {
        return true;
    }

    @Override
    protected void undo(Context context, ContextType type)
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
