package gent.timdemey.cards.model.commands;

import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.SolShowTestState;
import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.IScalableImageManager;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_CardsVisibleSwitch extends CommandBase
{

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return true;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        SolShowTestState testState = (SolShowTestState) state;
        List<Card> cards = testState.getCardGame().getCards();
        testState.cardsVisible = !testState.cardsVisible;
        
        for(Card card : cards)
        {
            Services.get(IScalableImageManager.class).getJScalableImage(card.id).setVisible(testState.cardsVisible); 
        }        
    }

    @Override
    public String toDebugString()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
