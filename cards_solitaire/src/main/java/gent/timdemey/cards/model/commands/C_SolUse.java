package gent.timdemey.cards.model.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardGame;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.boot.SolitaireCardStackType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_SolUse extends CommandBase
{
    public final UUID initiatorStackId;
    public final UUID initiatorCardId;

    public C_SolUse(UUID initiatorStackId, UUID initiatorCardId)
    {
        if((initiatorStackId == null && initiatorCardId == null) || (initiatorStackId != null && initiatorCardId != null))
        {
            throw new IllegalArgumentException("Choose exactly one initator for a Use command: a card, or a card stack, but not both.");
        }
        this.initiatorStackId = initiatorStackId;
        this.initiatorCardId = initiatorCardId;
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

            if(cardStackType.equals(SolitaireCardStackType.DEPOT))
            {
                if(initiatorStack.getCards().isEmpty()) // direction turnover -> depot, all cards
                {
                    CardStack srcCardStack = cardGame.getCardStack(localId, SolitaireCardStackType.TURNOVER, 0);
                    CardStack dstCardStack = cardGame.getCardStack(localId, SolitaireCardStackType.DEPOT, 0);
                    if(!srcCardStack.getCards().isEmpty())
                    {
                        eligible.add(new C_SolMove(srcCardStack.id, dstCardStack.id, srcCardStack.getLowestCard().id));
                    }
                }
                else // direction depot -> turnover, 1 card
                {
                    CardStack srcCardStack = cardGame.getCardStack(localId, SolitaireCardStackType.DEPOT, 0);
                    CardStack dstCardStack = cardGame.getCardStack(localId, SolitaireCardStackType.TURNOVER, 0);
                    Card card = srcCardStack.getHighestCard();
                    eligible.add(new C_SolMove(srcCardStack.id, dstCardStack.id, card.id));
                }
            }

        }
        else
        {
            Card initiatorCard = cardGame.getCard(initiatorCardId);
            CardStack initiatorStack = initiatorCard.cardStack;
            String cardStackType = initiatorStack.cardStackType;

            if(cardStackType.equals(SolitaireCardStackType.TURNOVER) || cardStackType.equals(SolitaireCardStackType.MIDDLE))
            {
                if(!initiatorStack.getCards().isEmpty())
                {
                    Card card = initiatorStack.getHighestCard();
                    if(card.visibleRef.get())
                    {
                        for (CardStack dstCardStack : cardGame.getCardStacks(localId, SolitaireCardStackType.LAYDOWN))
                        {
                            eligible.add(new C_SolMove(initiatorStack.id, dstCardStack.id, card.id));
                        }
                    }
                    else
                    {
                        List<Card> cards = initiatorStack.getCardsFrom(card);
                        eligible.add(new C_SetVisible(cards, true));
                    }
                }
            }
            else if (cardStackType.contentEquals(SolitaireCardStackType.DEPOT))
            {
                Card card = initiatorStack.getHighestCard();
                CardStack dstCardStack = cardGame.getCardStack(localId, SolitaireCardStackType.TURNOVER, 0);
                eligible.add(new C_SolMove(initiatorStack.id, dstCardStack.id, card.id));
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

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Client, ContextType.Server);

        CommandBase cmd = getUseCommand(context, type, state);
        return cmd != null;
    }

    protected void execute(Context context, ContextType type, State state)
    {
        CheckNotContext(type, ContextType.Client, ContextType.Server);

        CommandBase cmd = getUseCommand(context, type, state);
        schedule(ContextType.UI, cmd);
    };
}
