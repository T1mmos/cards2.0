package gent.timdemey.cards.model.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardGame;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.boot.SolShowCardStackType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_SolShowMove extends CommandBase
{
    private final UUID srcCardStackId;
    private final UUID dstCardStackId;
    private final UUID cardId;

    private List<Card> transferCards;
    private List<Card> flippedTransferCards;
    private boolean depotInvolved;
    private boolean visible;
    
    public C_SolShowMove(UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        this.srcCardStackId = srcCardStackId;
        this.dstCardStackId = dstCardStackId;
        this.cardId = cardId;
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        CardGame cardGame = state.getCardGame();
        CardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        CardStack dstCardStack = cardGame.getCardStack(dstCardStackId);
        Preconditions.checkArgument(srcCardStack.getCards().contains(cardId));

        Card card = state.getCardGame().getCard(cardId);

        List<UUID> toTransferIds = srcCardStack.getCardsFrom(card).getIds();
        C_SolShowPull cmdPull = new C_SolShowPull(srcCardStackId, cardId);
        C_SolShowPush cmdPush = new C_SolShowPush(dstCardStackId, toTransferIds);

        if(cmdPull.canExecute(context, type, state) && cmdPush.canExecute(context, type, state)) // user action
        {
            return true;
        }
        else // game logic action
        {
            UUID srcPlayerId = cardGame.getPlayerId(srcCardStack);
            UUID dstPlayerId = cardGame.getPlayerId(dstCardStack);
            if(!srcPlayerId.equals(dstPlayerId))
            {
                return false;
            }

            String srcCardStackType = srcCardStack.cardStackType;
            String dstCardStackType = dstCardStack.cardStackType;
            if(srcCardStackType.equals(SolShowCardStackType.DEPOT) && dstCardStackType.equals(SolShowCardStackType.TURNOVER))
            {
                return !srcCardStack.getCards().isEmpty() && srcCardStack.getHighestCard() == card;
            }
            else if(srcCardStackType.equals(SolShowCardStackType.TURNOVER) && dstCardStackType.equals(SolShowCardStackType.DEPOT))
            {
                return !srcCardStack.getCards().isEmpty() && dstCardStack.getCards().isEmpty() && srcCardStack.getLowestCard() == card;
            }
        }

        return false;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        UUID localId = state.getLocalId();
        CardGame cardGame = state.getCardGame();
        CardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        CardStack dstCardStack = cardGame.getCardStack(dstCardStackId);
        
        if(transferCards == null)
        {
            depotInvolved = (srcCardStack.cardStackType.equals(SolShowCardStackType.DEPOT)
                    && dstCardStack.cardStackType.equals(SolShowCardStackType.TURNOVER))
                    || (srcCardStack.cardStackType.equals(SolShowCardStackType.TURNOVER)
                            && dstCardStack.cardStackType.equals(SolShowCardStackType.DEPOT));
            visible = depotInvolved && dstCardStack.cardStackType.equals(SolShowCardStackType.TURNOVER);
            List<Card> cards = srcCardStack.getCards();
            Card card = srcCardStack.getCards().get(cardId);
            transferCards = new ArrayList<>(cards.subList(cards.indexOf(card), cards.size()));

            if(depotInvolved)
            {
                flippedTransferCards = new ArrayList<>(transferCards);
                Collections.reverse(flippedTransferCards);
            }
        }
        
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
        // TODO Auto-generated method stub
        return super.canUndo(context, type, state);
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
