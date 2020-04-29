package gent.timdemey.cards.model.entities.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.commands.C_SetVisible;
import gent.timdemey.cards.model.entities.commands.C_Use;
import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.boot.SolShowCardStackType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_SolShowUse extends C_Use
{    
    public C_SolShowUse(UUID initiatorStackId, UUID initiatorCardId)
    {
        super(initiatorStackId, initiatorCardId);
    }
    
    @Override
    protected CommandBase resolveCommand(Context context, ContextType type, State state)
    {        
        CardGame cardGame = state.getCardGame();
        List<CommandBase> eligible = new ArrayList<>();
        UUID localId = context.getReadOnlyState().getLocalId();
        

        if(initiatorStackId != null)
        {
            CardStack initiatorStack = cardGame.getCardStack(initiatorStackId);
            if (getSourceId().equals(cardGame.getPlayerId(initiatorStack)))
            {
                String cardStackType = initiatorStack.cardStackType;

                if(cardStackType.equals(SolShowCardStackType.DEPOT))
                {
                    addUseCommandDepot(eligible, initiatorStack, cardGame, localId);
                }
            }            
        }
        else // initiator is a Card
        {
            Card initiatorCard = cardGame.getCard(initiatorCardId);
            CardStack initiatorStack = initiatorCard.cardStack;
            String cardStackType = initiatorStack.cardStackType;

            if (getSourceId().equals(cardGame.getPlayerId(initiatorStack)))
            {
                if(cardStackType.equals(SolShowCardStackType.TURNOVER) || cardStackType.equals(SolShowCardStackType.MIDDLE) || cardStackType.equals(SolShowCardStackType.SPECIAL))
                {
                    if(!initiatorStack.getCards().isEmpty())
                    {
                        Card card = initiatorStack.getHighestCard();
                        if(card.visibleRef.get())
                        {
                            for (CardStack dstCardStack : cardGame.getCardStacks(SolShowCardStackType.LAYDOWN))
                            {
                                C_SolShowMove cmd = new C_SolShowMove(initiatorStack.id, dstCardStack.id, card.id);
                                cmd.setSourceId(getSourceId());
                                eligible.add(cmd);
                            }
                        }
                        else
                        {
                            List<Card> cards = initiatorStack.getCardsFrom(card);
                            C_SetVisible cmd = new C_SetVisible(cards, true);
                            cmd.setSourceId(getSourceId());
                            eligible.add(cmd);
                        }
                    }
                }
                else if (cardStackType.contentEquals(SolShowCardStackType.DEPOT))
                {
                    addUseCommandDepot(eligible, initiatorStack, cardGame, localId);
                }
            }
        }
        
        for (CommandBase cmd : eligible)
        {
            if(cmd.canExecute(state))
            {
                return cmd;
            }
        }
        return null;

    }
    
    private void addUseCommandDepot (List<CommandBase> eligible, CardStack initiatorStack, CardGame cardGame, UUID localId)
    {
        if(initiatorStack.getCards().isEmpty()) // direction turnover -> depot, all cards
        {
            CardStack srcCardStack = cardGame.getCardStack(localId, SolShowCardStackType.TURNOVER, 0);
            CardStack dstCardStack = cardGame.getCardStack(localId, SolShowCardStackType.DEPOT, 0);
            if(!srcCardStack.getCards().isEmpty())
            {
                C_SolShowMove cmd = new C_SolShowMove(srcCardStack.id, dstCardStack.id, srcCardStack.getLowestCard().id);
                cmd.setSourceId(getSourceId());
                eligible.add(cmd);
            }
        }
        else // direction depot -> turnover, 3 cards
        {
            CardStack srcCardStack = cardGame.getCardStack(localId, SolShowCardStackType.DEPOT, 0);
            CardStack dstCardStack = cardGame.getCardStack(localId, SolShowCardStackType.TURNOVER, 0);
            
            List<Card> availableCards = srcCardStack.getCards();
            int availableCount = availableCards.size();
            int takeCount = Math.min(3, availableCount);
            int idx = availableCount - takeCount;
            UUID highestCardId = availableCards.get(idx).id;
            C_SolShowMove cmd = new C_SolShowMove(srcCardStack.id, dstCardStack.id, highestCardId);
            cmd.setSourceId(getSourceId());
            eligible.add(cmd);
        }
    }

}
