package gent.timdemey.cards.model.entities.commands;

import java.util.List;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.SolShowTestState;
import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public class C_CardsVisibleSwitch extends CommandBase
{

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        return CanExecuteResponse.yes();
    }

    @Override
    protected void preExecute(Context context, ContextType type, State state)
    {
        SolShowTestState testState = (SolShowTestState) state;
        List<Card> cards = testState.getCardGame().getCards();
        testState.cardsVisible = !testState.cardsVisible;
        
        for(Card card : cards)
        {
            card.visibleRef.set(testState.cardsVisible);
        } 
        
        IScalingService scaleServ = Services.get(IScalingService.class);
        List<IScalableComponent> comps = scaleServ.getComponents();
        for(IScalableComponent comp : comps)
        {
            comp.update();
        }        
    }

    @Override
    public String toDebugString()
    {
        // TODO Auto-generated method stub
        return null;
    }

}
