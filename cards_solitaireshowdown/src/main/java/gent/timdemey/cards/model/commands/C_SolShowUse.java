package gent.timdemey.cards.model.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardGame;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.boot.SolShowCardStackType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_SolShowUse extends CommandBase
{
    public final UUID initiatorStackId;
    public final UUID initiatorCardId;
    
    public C_SolShowUse(UUID initiatorStackId, UUID initiatorCardId)
    {
        if((initiatorStackId == null && initiatorCardId == null) || (initiatorStackId != null && initiatorCardId != null))
        {
            throw new IllegalArgumentException("Choose exactly one initator for a Use command: a card, or a card stack, but not both.");
        }
        this.initiatorStackId = initiatorStackId;
        this.initiatorCardId = initiatorCardId;
    }
    
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        // a use command can only be run in the UI layer, because it translates to a different,
        // concrete command.
        CheckNotContext(type, ContextType.Client, ContextType.Server);
        
        CommandBase actualCommand = getUseCommand(context, type, state);
        return actualCommand != null;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CommandBase actualCommand = getUseCommand(context, type, state);
        schedule(type, actualCommand);
    }
    
    private CommandBase getUseCommand(Context context, ContextType type, State state)
    {
        CardGame cardGame = state.getCardGame();
        List<CommandBase> eligible = new ArrayList<>();
        UUID localId = context.getReadOnlyState().getLocalId();

        if(initiatorStackId != null)
        {
            CardStack initiatorStack = cardGame.getCardStack(initiatorStackId);

            String cardStackType = initiatorStack.cardStackType;

            if(cardStackType.equals(SolShowCardStackType.DEPOT))
            {
                addUseCommandDepot(eligible, initiatorStack, cardGame, localId);
            }
        }
        else // initiator is a Card
        {
            Card initiatorCard = cardGame.getCard(initiatorCardId);
            CardStack initiatorStack = initiatorCard.cardStack;
            String cardStackType = initiatorStack.cardStackType;

            if(cardStackType.equals(SolShowCardStackType.TURNOVER) || cardStackType.equals(SolShowCardStackType.MIDDLE) || cardStackType.equals(SolShowCardStackType.SPECIAL))
            {
                if(!initiatorStack.getCards().isEmpty())
                {
                    Card card = initiatorStack.getHighestCard();
                    if(card.visibleRef.get())
                    {
                        for (CardStack dstCardStack : cardGame.getCardStacks(localId, SolShowCardStackType.LAYDOWN))
                        {
                            eligible.add(new C_SolShowMove(initiatorStack.id, dstCardStack.id, card.id));
                        }
                    }
                    else
                    {
                        List<Card> cards = initiatorStack.getCardsFrom(card);
                        eligible.add(new C_SetVisible(cards, true));
                    }
                }
            }
            else if (cardStackType.contentEquals(SolShowCardStackType.DEPOT))
            {
                addUseCommandDepot(eligible, initiatorStack, cardGame, localId);
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
                eligible.add(new C_SolShowMove(srcCardStack.id, dstCardStack.id, srcCardStack.getLowestCard().id));
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
            eligible.add(new C_SolShowMove(srcCardStack.id, dstCardStack.id, highestCardId));
        }
    }

}
