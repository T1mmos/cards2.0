package gent.timdemey.cards.model.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.commands.payload.P_SolShowMove;
import gent.timdemey.cards.model.entities.cards.Card;
import gent.timdemey.cards.model.entities.cards.CardGame;
import gent.timdemey.cards.model.entities.cards.CardStack;
import gent.timdemey.cards.model.entities.commands.C_Move;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.serialization.mappers.CommandDtoMapper;
import gent.timdemey.cards.services.boot.SolShowCardStackType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_SolShowMove extends C_Move
{
    private List<Card> flippedTransferCards;
    private boolean depotInvolved;
    private boolean visible;

    public C_SolShowMove(UUID srcCardStackId, UUID dstCardStackId, UUID cardId)
    {
        super(srcCardStackId, dstCardStackId, cardId);
    }

    public C_SolShowMove(P_SolShowMove pl)
    {
        super(pl);
    }

    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        if(type == ContextType.Client)
        {
            return true;
        }

        CardGame cardGame = state.getCardGame();
        CardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        CardStack dstCardStack = cardGame.getCardStack(dstCardStackId);

        if(!srcCardStack.getCards().contains(cardId))
        {
            return false;
        }

        Card card = state.getCardGame().getCard(cardId);

        List<UUID> toTransferIds = srcCardStack.getCardsFrom(card).getIds();
        C_SolShowPull cmdPull = new C_SolShowPull(srcCardStackId, cardId);
        cmdPull.setSourceId(getSourceId());
        C_SolShowPush cmdPush = new C_SolShowPush(dstCardStackId, toTransferIds);
        cmdPush.setSourceId(getSourceId());

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
                if(srcCardStack.getCards().isEmpty())
                {
                    return false;
                }
                if(srcCardStack.cards.indexOf(card) < srcCardStack.cards.size() - 3)
                {
                    return false;
                }

                return true;
            }
            else if(srcCardStackType.equals(SolShowCardStackType.TURNOVER) && dstCardStackType.equals(SolShowCardStackType.DEPOT))
            {
                if(srcCardStack.getCards().isEmpty())
                {
                    return false;
                }
                if(!dstCardStack.getCards().isEmpty())
                {
                    return false;
                }
                if(srcCardStack.getLowestCard() != card)
                {
                    return false;
                }

                return true;
            }
        }

        return false;
    }

    @Override
    protected void execute(Context context, ContextType type, State state)
    {
        if(type == ContextType.UI || type == ContextType.Server)
        {
            CardGame cardGame = state.getCardGame();
            CardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
            CardStack dstCardStack = cardGame.getCardStack(dstCardStackId);

            if(transferCards == null)
            {
                depotInvolved = (srcCardStack.cardStackType.equals(SolShowCardStackType.DEPOT) && dstCardStack.cardStackType.equals(
                    SolShowCardStackType.TURNOVER)) || (srcCardStack.cardStackType.equals(SolShowCardStackType.TURNOVER) && dstCardStack.cardStackType.equals(
                        SolShowCardStackType.DEPOT));
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
                srcCardStack.removeAll(flippedTransferCards);
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
                srcCardStack.removeAll(transferCards);
                dstCardStack.addAll(transferCards);
            }

            transferCards.forEach(card -> card.cardStack = dstCardStack);

            if(type == ContextType.UI)
            {
                forward(type, state);
            }
            else if(type == ContextType.Server)
            {
                List<UUID> ids_move = state.getPlayers().getExceptUUID(getSourceId());
                state.getTcpConnectionPool().broadcast(ids_move, getSerialized());

                // end of game trigger
                if(srcCardStack.cardStackType.equals(SolShowCardStackType.SPECIAL) && srcCardStack.getCards().size() == 0)
                {
                    state.setGameState(GameState.Ended);                    
                    UUID winnerId = cardGame.getPlayerId(srcCardStack);
                    C_SolShowOnEndGame cmd_endgame = new C_SolShowOnEndGame(winnerId);
                    String ser_endgame = getCommandDtoMapper().toJson(cmd_endgame);
                    List<UUID> ids_endgame = state.getPlayers().getIds();
                    state.getTcpConnectionPool().broadcast(ids_endgame, ser_endgame);
                }
            }
        }

        else if(type == ContextType.Client)
        {
            forward(type, state);
        }

    }

    @Override
    protected boolean canUndo(Context context, ContextType type, State state)
    {
        return true;
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
