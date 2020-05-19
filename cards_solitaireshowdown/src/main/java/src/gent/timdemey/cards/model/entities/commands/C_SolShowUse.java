package gent.timdemey.cards.model.entities.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
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
            
            boolean isTurnOver = cardStackType.equals(SolShowCardStackType.TURNOVER);
            boolean isMiddle = cardStackType.equals(SolShowCardStackType.MIDDLE);
            boolean isSpecial = cardStackType.equals(SolShowCardStackType.SPECIAL);
            boolean isDepot = cardStackType.contentEquals(SolShowCardStackType.DEPOT);
            
            // can only move from a stack of your own
            if (!localId.equals(cardGame.getPlayerId(initiatorStack)))
            {
                return null;
            }
            
            // the SPECIAL stack's highest card can be invisible as long as the server didn't accept
            // a previous move that involved the SPECIAL stack, to not reveal any cards until the previously
            // highest card has completely moved away from this stack. 
            if(isSpecial && !initiatorCard.visibleRef.get())
            {
                return null;
            }
            
            // a player cannot "use" a card that is not the highest of the stack
            // (of these cardstacks, only the turnover stack shows some lower cards in the UI)
            Card highestCard = initiatorStack.getHighestCard();
            if (highestCard != initiatorCard)
            {
                return null;
            }
            
            if(isTurnOver || isMiddle || isSpecial)
            {
                List<UUID> otherPlayerIds = state.getPlayers().getExceptUUID(localId);
                UUID otherPlayerId = otherPlayerIds.get(0);
                List<CardStack> allLaydownStacks = cardGame.getCardStacks(localId, SolShowCardStackType.LAYDOWN);
                List<CardStack> otherLaydownStacks = cardGame.getCardStacks(otherPlayerId, SolShowCardStackType.LAYDOWN);
                allLaydownStacks.addAll(otherLaydownStacks);
                for (CardStack dstCardStack : allLaydownStacks)
                {
                    C_SolShowMove cmd = new C_SolShowMove(initiatorStack.id, dstCardStack.id, initiatorCardId);
                    cmd.setSourceId(getSourceId());
                    eligible.add(cmd);
                }
            }
            else if (isDepot)
            {
                addUseCommandDepot(eligible, initiatorStack, cardGame, localId);
            }
        }
        
        for (CommandBase cmd : eligible)
        {
            if(cmd.canExecute(state).canExecute())
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
