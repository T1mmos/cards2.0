package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import java.util.ArrayList;
import java.util.List;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_SetVisible;
import gent.timdemey.cards.utils.Debug;

public class C_SetVisible extends CommandBase
{
    private final List<Card> cards;
    private final boolean visible;

    public C_SetVisible(
        Container container,
        P_SetVisible parameters)
    {
        super(container, parameters);
        
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
    public CanExecuteResponse canExecute()
    {
        return CanExecuteResponse.yes();
    }

    @Override
    public void execute()
    {
        for (Card card : cards)
        {
            card.visibleRef.set(visible);
        }
    }

    @Override
    public boolean canUndo()
    {
        return true;
    }

    @Override
    public void undo()
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
