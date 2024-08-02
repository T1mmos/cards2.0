package gent.timdemey.cards.model.entities.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.entities.state.Card;
import gent.timdemey.cards.model.entities.state.CardGame;
import gent.timdemey.cards.model.entities.state.CardStack;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_Move;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityFactory;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityList;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.interfaces.ICardGameService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.INetworkService;

public class C_SolShowMove extends C_Move
{    
    private List<Card> flippedTransferCards;
    private boolean depotInvolved;
    private boolean visible;
    private final INetworkService _NetworkService;
    private final CommandFactory _CommandFactory;
    private final ICardGameService _CardGameService;

    public C_SolShowMove(
        IContextService contextService, INetworkService networkService, CommandFactory commandFactory, ICardGameService cardGameService, State state,
        P_Move parameters)
    {
        super(contextService, state, parameters);
        
        this._CommandFactory = commandFactory;
        this._NetworkService = networkService;
        this._CardGameService = cardGameService;
    }

    @Override
    protected CanExecuteResponse canExecute(Context context, ContextType type)
    {
        CanExecuteResponse supResp = super.canExecute(context, type);
        if (!supResp.canExecute())
        {
            return supResp;
        }

        CardGame cardGame = _State.getCardGame();
        CardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        CardStack dstCardStack = cardGame.getCardStack(dstCardStackId);

        if (!srcCardStack.getCards().contains(cardId))
        {
            return CanExecuteResponse.no("Source stack doesn't contain the card with id " + cardId);
        }

        Card card = _State.getCardGame().getCard(cardId);

        List<UUID> toTransferIds = srcCardStack.getCardsFrom(card).getIds();
        C_Pull cmdPull = _CommandFactory.CreatePull(srcCardStackId, cardId);
        cmdPull.setSourceId(getSourceId());
        C_Push cmdPush = _CommandFactory.CreatePush(dstCardStackId, toTransferIds);
        cmdPush.setSourceId(getSourceId());

        boolean canPull = cmdPull.canExecute(context, type).canExecute();
        boolean canPush = cmdPush.canExecute(context, type).canExecute();
        if (canPull && canPush) // user action
        {
            return CanExecuteResponse.yes();
        }
        else // game logic action
        {
            UUID srcPlayerId = cardGame.getPlayerId(srcCardStack);
            UUID dstPlayerId = cardGame.getPlayerId(dstCardStack);
            if (!srcPlayerId.equals(dstPlayerId))
            {
                return CanExecuteResponse.no("SrcPlayerId != DstPlayerId");
            }

            String srcCardStackType = srcCardStack.cardStackType;
            String dstCardStackType = dstCardStack.cardStackType;
            if (srcCardStackType.equals(SolShowCardStackType.DEPOT)
                    && dstCardStackType.equals(SolShowCardStackType.TURNOVER))
            {
                if (srcCardStack.getCards().isEmpty())
                {
                    return CanExecuteResponse.no("Source stack DEPOT is empty");
                }
                if (srcCardStack.cards.indexOf(card) < srcCardStack.cards.size() - 3)
                {
                    return CanExecuteResponse.no("The card to move from DEPOT to TURNOVER must be the third highest card");
                }

                return CanExecuteResponse.yes();
            }
            else if (srcCardStackType.equals(SolShowCardStackType.TURNOVER)
                    && dstCardStackType.equals(SolShowCardStackType.DEPOT))
            {
                if (srcCardStack.getCards().isEmpty())
                {
                    return CanExecuteResponse.no("Source stack TURNOVER is empty");
                }
                if (!dstCardStack.getCards().isEmpty())
                {
                    return CanExecuteResponse.no("Destination stack DEPOT is not empty");
                }
                if (srcCardStack.getLowestCard() != card)
                {
                    return CanExecuteResponse.no("TURNOVER stack's lowest card is not the intended card to move");
                }

                return CanExecuteResponse.yes();
            }
        }

        return CanExecuteResponse.no("This is not a valid Solitaire Showdown move command");
    }

    @Override
    protected void execute(Context context, ContextType type)
    {
        CardGame cardGame = _State.getCardGame();
        CardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        CardStack dstCardStack = cardGame.getCardStack(dstCardStackId);

        if (transferCards == null)
        {
            depotInvolved = (srcCardStack.cardStackType.equals(SolShowCardStackType.DEPOT)
                    && dstCardStack.cardStackType.equals(SolShowCardStackType.TURNOVER))
                    || (srcCardStack.cardStackType.equals(SolShowCardStackType.TURNOVER)
                            && dstCardStack.cardStackType.equals(SolShowCardStackType.DEPOT));
            visible = depotInvolved && dstCardStack.cardStackType.equals(SolShowCardStackType.TURNOVER);
            List<Card> cards = srcCardStack.getCards();
            Card card = srcCardStack.getCards().get(cardId);
            transferCards = new ArrayList<>(cards.subList(cards.indexOf(card), cards.size()));

            if (depotInvolved)
            {
                flippedTransferCards = new ArrayList<>(transferCards);
                Collections.reverse(flippedTransferCards);
            }
        }

        if (depotInvolved)
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

        // calculate score
        addScore(_State, cardGame, srcCardStack, dstCardStack, false);
        
        if (type == ContextType.UI)
        {
            _NetworkService.send(_State.getLocalId(), _State.getServerId(), this, _State.getTcpConnectionPool());
        }
        else if (type == ContextType.Server)
        {
            List<UUID> ids_move = _State.getPlayers().getExceptUUID(getSourceId());
            _State.getTcpConnectionPool().broadcast(ids_move, getSerialized());

            // end of game trigger
            if (srcCardStack.cardStackType.equals(SolShowCardStackType.SPECIAL) && srcCardStack.getCards().size() == 0)
            {
                _State.setGameState(GameState.Ended);
                UUID winnerId = cardGame.getPlayerId(srcCardStack);
                
                
                C_OnGameEnded cmd_endgame = _CommandFactory.CreateOnGameEnded(winnerId);

                List<UUID> ids_endgame = _State.getPlayers().getIds();
                _NetworkService.broadcast(_State.getLocalId(), ids_endgame, cmd_endgame, _State.getTcpConnectionPool());
            }
        }
    }
    
    @Override
    public void onAccepted(Context context, ContextType type)
    {
        CardGame cardGame = _State.getCardGame();
        CardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        if (srcCardStack.cardStackType.equals(SolShowCardStackType.SPECIAL) && srcCardStack.cards.size() > 0)
        {
            srcCardStack.getHighestCard().visibleRef.set(true);
        }
    }

    @Override
    protected boolean canUndo(Context context, ContextType type)
    {
        return true;
    }

    private void addScore(State state, CardGame cardGame, CardStack srcCardStack, CardStack dstCardStack, boolean undo)
    {
        ReadOnlyEntityList<ReadOnlyCard> roTransferCards = ReadOnlyEntityFactory.getOrCreateCardList(transferCards);
        ReadOnlyCardStack roSrcCardStack = ReadOnlyEntityFactory.getOrCreateCardStack(srcCardStack);
        ReadOnlyCardStack roDstCardStack = ReadOnlyEntityFactory.getOrCreateCardStack(dstCardStack);
        
        int score = _CardGameService.getScore(roSrcCardStack, roDstCardStack, roTransferCards);
        
        if (undo)
        {
            score *= -1;
        }
        
        UUID playerId = cardGame.getPlayerId(srcCardStack);
        state.getPlayers().get(playerId).addScore(score);
        
        Card highestTransferCard = transferCards.get(0);
        highestTransferCard.scoreRef.set(highestTransferCard.scoreRef.get() + score);
    }
    
    @Override
    protected void undo(Context context, ContextType type)
    {
        CardGame cardGame = _State.getCardGame();
        CardStack srcCardStack = cardGame.getCardStacks().get(srcCardStackId);
        CardStack dstCardStack = cardGame.getCardStacks().get(dstCardStackId);      

        addScore(_State, cardGame, srcCardStack, dstCardStack, true);
        
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
    
    public CommandType getCommandType()
    {
        return CommandType.SYNCED;
    }
}
