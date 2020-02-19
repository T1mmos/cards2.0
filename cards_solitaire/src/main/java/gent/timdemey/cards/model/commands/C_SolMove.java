package gent.timdemey.cards.model.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardGame;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.boot.SolitaireCardStackType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Solitaire specific move command.
 * 
 * @author Tim
 */
public class C_SolMove extends CommandBase
{
    public final UUID srcCardStackId;
    public final UUID dstCardStackId;
    public final UUID cardId;

    private List<Card> transferCards;
    private List<Card> flippedTransferCards;
    private boolean depotInvolved;
    private boolean visible;

    public C_SolMove(UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        this.srcCardStackId = srcCardStackId;
        this.dstCardStackId = dstCardStackId;
        this.cardId = cardId;
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        CardStack srcCardStack = state.getCardGame().getCardStacks().get(srcCardStackId);
        CardStack dstCardStack = state.getCardGame().getCardStacks().get(dstCardStackId);
        Card card = state.getCardGame().getCard(cardId);

        List<UUID> toTransferIds = srcCardStack.getCardsFrom(card).getIds();
        C_SolPull cmdPull = new C_SolPull(srcCardStackId, cardId);
        C_SolPush cmdPush = new C_SolPush(dstCardStackId, toTransferIds);
        if(cmdPull.canExecute(context, type, state) && cmdPush.canExecute(context, type, state)) // user action
        {
            return true;
        }
        else // game logic action
        {
            String srcCardStackType = srcCardStack.cardStackType;
            String dstCardStackType = dstCardStack.cardStackType;
            if(srcCardStackType.equals(SolitaireCardStackType.DEPOT)
                    && dstCardStackType.equals(SolitaireCardStackType.TURNOVER))
            {
                return !srcCardStack.getCards().isEmpty() && srcCardStack.getHighestCard() == card;
            }
            else if(srcCardStackType.equals(SolitaireCardStackType.TURNOVER)
                    && dstCardStackType.equals(SolitaireCardStackType.DEPOT))
            {
                return !srcCardStack.getCards().isEmpty() && dstCardStack.getCards().isEmpty()
                        && srcCardStack.getLowestCard() == card;
            }
        }
        return false;

    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        CardGame cardGame = state.getCardGame();
        CardStack srcCardStack = cardGame.getCardStacks().get(srcCardStackId);
        CardStack dstCardStack = cardGame.getCardStacks().get(dstCardStackId);

        if(transferCards == null)
        {
            depotInvolved = (srcCardStack.cardStackType.equals(SolitaireCardStackType.DEPOT)
                    && dstCardStack.cardStackType.equals(SolitaireCardStackType.TURNOVER))
                    || (srcCardStack.cardStackType.equals(SolitaireCardStackType.TURNOVER)
                            && dstCardStack.cardStackType.equals(SolitaireCardStackType.DEPOT));
            visible = depotInvolved && dstCardStack.cardStackType.equals(SolitaireCardStackType.TURNOVER);
            List<Card> cards = srcCardStack.getCards();
            Card card = srcCardStack.getCards().get(cardId);
            transferCards = new ArrayList<>(cards.subList(cards.indexOf(card), cards.size()));

            if(depotInvolved)
            {
                flippedTransferCards = new ArrayList<>(transferCards);
                Collections.reverse(flippedTransferCards);
            }
        }

        srcCardStack.getCards().removeAll(transferCards);

        if(depotInvolved)
        {
            dstCardStack.addAll(flippedTransferCards);
            // transfering to depot -> cards become invisible, transfering to turnover ->
            // cards become visible
            for (Card card : transferCards)
            {
                card.visibleRef.set(visible);
            }
        }
        else
        {
            dstCardStack.addAll(transferCards);
        }

        transferCards.forEach(card -> card.cardStack = dstCardStack);

        if(type == ContextType.Server)
        {
            List<UUID> idsToUpdate = state.getPlayers().getExceptUUID(getSourceId());
            state.getTcpConnectionPool().broadcast(idsToUpdate, getSerialized());
        }
    }

    @Override
    protected boolean canUndo(Context context, ContextType type, State state)
    {
        CardStack dstCardStack = state.getCardGame().getCardStacks().get(dstCardStackId);

        return dstCardStack.getCards().contains(cardId);
    }

    @Override
    protected void undo(Context context, ContextType type, State state)
    {
        CardGame cardGame = state.getCardGame();
        CardStack srcCardStack = cardGame.getCardStacks().get(srcCardStackId);
        CardStack dstCardStack = cardGame.getCardStacks().get(dstCardStackId);

        // for removal, it doesn't really matter which list we take
        if(depotInvolved)
        {
            dstCardStack.getCards().removeAll(flippedTransferCards);
            for (Card card : flippedTransferCards)
            {
                card.visibleRef.set(!visible);
            }
        }
        else
        {
            dstCardStack.getCards().removeAll(transferCards);
        }

        srcCardStack.addAll(transferCards);
        transferCards.forEach(card -> card.cardStack = srcCardStack);
    }
}
