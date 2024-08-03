package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.di.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Move;
import gent.timdemey.cards.services.contract.descriptors.SolitaireComponentTypes;

/**
 * Solitaire specific move command.
 * 
 * @author Tim
 */
public class C_SolMove extends C_Move
{
    private List<Card> flippedTransferCards;
    private boolean depotInvolved;
    private boolean visible;
    private final CommandFactory _CommandFactory;

    public C_SolMove(
        Container container, CommandFactory commandFactory, 
        P_Move parameters)
    {
        super(container, parameters);
        
        this._CommandFactory = commandFactory;
    }

    @Override
    public CanExecuteResponse canExecute()
    {
        CardStack srcCardStack = _State.getCardGame().getCardStacks().get(srcCardStackId);
        CardStack dstCardStack = _State.getCardGame().getCardStacks().get(dstCardStackId);
        Card card = _State.getCardGame().getCard(cardId);

        List<UUID> toTransferIds = srcCardStack.getCardsFrom(card).getIds();
        C_Pull cmdPull = _CommandFactory.CreatePull(srcCardStackId, cardId);
        C_Push cmdPush = _CommandFactory.CreatePush(dstCardStackId, toTransferIds);
        boolean canPull = cmdPull.canExecute().canExecute();
        boolean canPush = cmdPush.canExecute().canExecute();
        if (canPull && canPush) // user action
        {
            return CanExecuteResponse.yes();
        }
        else // game logic action
        {
            String srcCardStackType = srcCardStack.cardStackType;
            String dstCardStackType = dstCardStack.cardStackType;
            if (srcCardStackType.equals(SolitaireComponentTypes.DEPOT)
                    && dstCardStackType.equals(SolitaireComponentTypes.TURNOVER))
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
            else if (srcCardStackType.equals(SolitaireComponentTypes.TURNOVER)
                    && dstCardStackType.equals(SolitaireComponentTypes.DEPOT))
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
    public void execute()
    {
        CardGame cardGame = _State.getCardGame();
        CardStack srcCardStack = cardGame.getCardStacks().get(srcCardStackId);
        CardStack dstCardStack = cardGame.getCardStacks().get(dstCardStackId);

        if (transferCards == null)
        {
            depotInvolved = (srcCardStack.cardStackType.equals(SolitaireComponentTypes.DEPOT)
                    && dstCardStack.cardStackType.equals(SolitaireComponentTypes.TURNOVER))
                    || (srcCardStack.cardStackType.equals(SolitaireComponentTypes.TURNOVER)
                            && dstCardStack.cardStackType.equals(SolitaireComponentTypes.DEPOT));
            visible = depotInvolved && dstCardStack.cardStackType.equals(SolitaireComponentTypes.TURNOVER);
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
    public boolean canUndo()
    {
        CardStack dstCardStack = _State.getCardGame().getCardStacks().get(dstCardStackId);

        return dstCardStack.getCards().contains(cardId);
    }

    @Override
    public void undo()
    {
        CardGame cardGame = _State.getCardGame();
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
