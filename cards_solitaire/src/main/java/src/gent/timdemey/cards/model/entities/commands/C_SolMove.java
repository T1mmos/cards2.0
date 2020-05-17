package gent.timdemey.cards.model.entities.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.commands.C_Move;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.cardgame.SolitaireCardStackType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

/**
 * Solitaire specific move command.
 * 
 * @author Tim
 */
public class C_SolMove extends C_Move
{
    private List<Card> transferCards;
    private List<Card> flippedTransferCards;
    private boolean depotInvolved;
    private boolean visible;

    public C_SolMove(UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        super(srcCardStackId, dstCardStackId, cardId);
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type, State state)
    {
        CardStack srcCardStack = state.getCardGame().getCardStacks().get(srcCardStackId);
        CardStack dstCardStack = state.getCardGame().getCardStacks().get(dstCardStackId);
        Card card = state.getCardGame().getCard(cardId);

        List<UUID> toTransferIds = srcCardStack.getCardsFrom(card).getIds();
        C_SolPull cmdPull = new C_SolPull(srcCardStackId, cardId);
        C_SolPush cmdPush = new C_SolPush(dstCardStackId, toTransferIds);
        boolean canPull = cmdPull.canExecute(context, type, state).canExecute();
        boolean canPush = cmdPush.canExecute(context, type, state).canExecute();
        if (canPull && canPush) // user action
        {
            return CanExecuteResponse.yes();
        }
        else // game logic action
        {
            String srcCardStackType = srcCardStack.cardStackType;
            String dstCardStackType = dstCardStack.cardStackType;
            if (srcCardStackType.equals(SolitaireCardStackType.DEPOT)
                    && dstCardStackType.equals(SolitaireCardStackType.TURNOVER))
            {
                if (srcCardStack.getCards().isEmpty())
                {
                    return CanExecuteResponse.no("The DEPOT stack is empty");
                }
                if (srcCardStack.getHighestCard() != card)
                {
                    String msg = "The DEPOT stack's highest card (%s) is not the intended card to move (%s)";
                    String highCardNot = srcCardStack.getHighestCard().getNotation();
                    String intdCardNot = card.getNotation();
                    String format = String.format(msg, highCardNot, intdCardNot);
                    return CanExecuteResponse.no(format);
                }
                return CanExecuteResponse.yes();
            }
            else if (srcCardStackType.equals(SolitaireCardStackType.TURNOVER)
                    && dstCardStackType.equals(SolitaireCardStackType.DEPOT))
            {
                if (srcCardStack.getCards().isEmpty())
                {
                    return CanExecuteResponse.no("The TURNOVER stack is empty");
                }
                if (!dstCardStack.getCards().isEmpty())
                {
                    return CanExecuteResponse.no("The DEPOT stack isn't empty");
                }
                if (srcCardStack.getLowestCard() != card)
                {
                    String msg = "The TURNOVER stack's lowest card (%s) is not the intended card to move (%s)";
                    String highCardNot = srcCardStack.getHighestCard().getNotation();
                    String intdCardNot = card.getNotation();
                    String format = String.format(msg, highCardNot, intdCardNot);
                    return CanExecuteResponse.no(format);
                }
                return CanExecuteResponse.yes();
            }
        }
        
        String srcCsType = srcCardStack.cardStackType;
        String dstCsType = dstCardStack.cardStackType;
        String format = String.format("This move (%s -> %s) is not allowed in Solitaire ", srcCsType, dstCsType);
        return CanExecuteResponse.no(format);
    }

    @Override
    protected void preExecute(Context context, ContextType type, State state)
    {
        CardGame cardGame = state.getCardGame();
        CardStack srcCardStack = cardGame.getCardStacks().get(srcCardStackId);
        CardStack dstCardStack = cardGame.getCardStacks().get(dstCardStackId);

        if (transferCards == null)
        {
            depotInvolved = (srcCardStack.cardStackType.equals(SolitaireCardStackType.DEPOT)
                    && dstCardStack.cardStackType.equals(SolitaireCardStackType.TURNOVER))
                    || (srcCardStack.cardStackType.equals(SolitaireCardStackType.TURNOVER)
                            && dstCardStack.cardStackType.equals(SolitaireCardStackType.DEPOT));
            visible = depotInvolved && dstCardStack.cardStackType.equals(SolitaireCardStackType.TURNOVER);
            List<Card> cards = srcCardStack.getCards();
            Card card = srcCardStack.getCards().get(cardId);
            transferCards = new ArrayList<>(cards.subList(cards.indexOf(card), cards.size()));

            if (depotInvolved)
            {
                flippedTransferCards = new ArrayList<>(transferCards);
                Collections.reverse(flippedTransferCards);
            }
        }

        srcCardStack.getCards().removeAll(transferCards);

        if (depotInvolved)
        {
            dstCardStack.addAll(flippedTransferCards);
            // transfering to depot -> cards become invisible, transfering to turnover ->
            // cards become visible
            for (Card card : transferCards)
            {
                card.cardStack = dstCardStack;
                card.visibleRef.set(visible);
            }
        }
        else
        {
            dstCardStack.addAll(transferCards);
            transferCards.forEach(card -> card.cardStack = dstCardStack);
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
        if (depotInvolved)
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
