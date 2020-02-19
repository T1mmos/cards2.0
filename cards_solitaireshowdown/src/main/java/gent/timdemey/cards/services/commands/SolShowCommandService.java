package gent.timdemey.cards.services.commands;

import java.rmi.server.Operation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.commands.C_Composite;
import gent.timdemey.cards.model.commands.C_SetVisible;
import gent.timdemey.cards.model.commands.C_StartGame;
import gent.timdemey.cards.multiplayer.CreateServerInfo;
import gent.timdemey.cards.services.ICommandService;
import gent.timdemey.cards.services.boot.SolShowCardStackType;
import gent.timdemey.cards.services.context.ContextType;

public class SolShowCommandService implements ICommandService {

    /**
     * Mocks a server connection by allowing immediately to start a game without joining a server lobby.
     * The other player ID is fixed ("FFFF-.....").
     */
    @Override
    public void startGame() {
        Preconditions.checkState(canStartGame());
        ICardGameCreator creator = Services.get(ICardGameCreator.class);
        List<List<E_Card>> cards = creator.getCards();        
        
        List<UUID> playerIds = Arrays.asList(getThreadContext().getLocalId(), UUID.fromString("FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF"));
        Map<UUID, List<E_CardStack>> playerStacks = creator.createStacks(playerIds, cards);
                
        C_StartGame command = new C_StartGame(playerStacks);
        getCommandProcessor().schedule(command);
    }
    
    @Override
    public boolean canMove(E_CardStack srcCardStack, E_CardStack dstCardStack, E_Card card) {
        Preconditions.checkArgument(srcCardStack.getCards().contains(card));
        
        List<E_Card> toTransfer = srcCardStack.getCardsFrom(card);
        if (canPull(srcCardStack, card) && canPush(dstCardStack, toTransfer))   // user action
        {
            return true;
        }
        else                                                                    // game logic action
        {
            E_CardGame cardGame = Services.get(IContextProvider.class).getThreadContext().getCardGameState().getCardGame();
            UUID srcPlayerId = cardGame.getPlayerId(srcCardStack);
            UUID dstPlayerId = cardGame.getPlayerId(dstCardStack);
            if (!srcPlayerId.equals(dstPlayerId))
            {
                return false;
            }
            
            String srcCardStackType = srcCardStack.getCardStackType();
            String dstCardStackType = dstCardStack.getCardStackType();
            if (srcCardStackType.equals(SolShowCardStackType.DEPOT) && dstCardStackType.equals(SolShowCardStackType.TURNOVER))
            {
                return !srcCardStack.isEmpty() && srcCardStack.getHighestCard() == card;
            }  
            else if (srcCardStackType.equals(SolShowCardStackType.TURNOVER) && dstCardStackType.equals(SolShowCardStackType.DEPOT))
            {
                return !srcCardStack.isEmpty() && dstCardStack.isEmpty() && srcCardStack.getLowestCard() == card;
            }  
        }
        return false;
    }

    @Override
    public boolean canPull(E_CardStack cardStack, E_Card card) {
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        E_CardGame cardGame = context.getCardGameState().getCardGame();
        boolean isLocal = context.isLocal(cardGame.getPlayerId(cardStack));
        
        if (!isLocal)
        {
            return false; // cannot alter other player's stacks (also not the common stacks)
        }
        
        String cardStackType = cardStack.getCardStackType();
        if (cardStackType.equals(SolShowCardStackType.DEPOT) || cardStackType.equals(SolShowCardStackType.LAYDOWN))
        {
            return false;
        }        
        if (cardStackType.equals(SolShowCardStackType.SPECIAL) || cardStackType.equals(SolShowCardStackType.TURNOVER))
        {
            return cardStack.getCardCountFrom(card) == 1;
        }
        if (cardStackType.equals(SolShowCardStackType.MIDDLE))
        {
            return true; // cards should always be in correct order, so should always be dragged from any card
        }
        
        throw new UnsupportedOperationException("No such stack type: " + cardStackType);
    }

    @Override
    public boolean canPush(E_CardStack dstCardStack, List<E_Card> srcCards) {
        Preconditions.checkNotNull(dstCardStack);
        Preconditions.checkNotNull(srcCards);
        Preconditions.checkArgument(!srcCards.isEmpty());
        
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        E_CardGame cardGame = context.getCardGameState().getCardGame();
        
        UUID localId = context.getLocalId();
        UUID dstPlayerId = cardGame.getPlayerId(dstCardStack);
        String dstType = dstCardStack.getCardStackType();
        
        if (!localId.equals(dstPlayerId) && !dstType.equals(SolShowCardStackType.LAYDOWN))
        {
            return false;
        }
        
        if (dstType.equals(SolShowCardStackType.MIDDLE))
        {
            if (dstCardStack.isEmpty()
                   ||
               !dstCardStack.isEmpty()  
                   && dstCardStack.getHighestCard().getCardColor() != srcCards.get(0).getCardColor()
                   && dstCardStack.getHighestCard().getCardValue().getOrderAtoK() == srcCards.get(0).getCardValue().getOrderAtoK() + 1
               )
            {
                return true;    
            }            
        }
        else if (dstType.equals(SolShowCardStackType.LAYDOWN))
        {
            if (srcCards.size() == 1)
            {
                if (dstCardStack.isEmpty() 
                        && srcCards.get(0).getCardValue() == E_CardValue.V_A
                        ||
                    !dstCardStack.isEmpty()  
                        && dstCardStack.getHighestCard().getCardSuit() == srcCards.get(0).getCardSuit()
                        && dstCardStack.getHighestCard().getCardValue().getOrderAtoK() + 1 == srcCards.get(0).getCardValue().getOrderAtoK()
                    )
                 {
                     return true;    
                 } 
            }
            
        }
        
        return false;
    }

    @Override
    public List<GameMove> getUseMoves(E_CardStack initiatorStack) {
        ContextFull context = Services.get(IContextProvider.class).getThreadContext();
        E_CardGame cardGame = context.getCardGameState().getCardGame();
        UUID playerId = cardGame.getPlayerId(initiatorStack);
        List<UUID> remoteIds = context.getRemotePlayerIds();
        
        Preconditions.checkState(remoteIds.size() == 1);
        
        UUID remoteId = remoteIds.get(0);
        UUID localId = context.getLocalId();
        boolean isLocal = context.isLocal(playerId);
        
        if (!isLocal)
        {
            return Collections.emptyList(); // cannot "use" other player's stacks
        }
        
        List<GameMove> allPossibleMoves = new ArrayList<>();
        String cardStackType = initiatorStack.getCardStackType();
        if (cardStackType.equals(SolShowCardStackType.DEPOT))
        {
            E_CardStack otherCardStack = cardGame.getCardStack(playerId, SolShowCardStackType.TURNOVER, 0);  
            if (initiatorStack.isEmpty())   // direction turnover -> depot, all cards
            {
                if (!otherCardStack.isEmpty() && canMove(otherCardStack, initiatorStack, otherCardStack.getLowestCard()))
                {
                    allPossibleMoves.add(new GameMove(Operation.Move, otherCardStack, initiatorStack, otherCardStack.getLowestCard()));
                }
            }
            else                            // direction depot -> turnover, 1 card
            {
                E_Card card = initiatorStack.getHighestCard();
                if (canMove(initiatorStack, otherCardStack, card))
                {
                    allPossibleMoves.add(new GameMove(Operation.Move, initiatorStack, otherCardStack, card));
                }
            }
        } 
        else if (cardStackType.equals(SolShowCardStackType.TURNOVER) 
                || cardStackType.equals(SolShowCardStackType.MIDDLE) 
                || cardStackType.equals(SolShowCardStackType.SPECIAL))
        {
            if (!initiatorStack.isEmpty())
            {
                E_Card card = initiatorStack.getHighestCard();
                if (card.isVisible())
                {
                    List<E_CardStack> allLaydownStacks = new ArrayList<>();
                    allLaydownStacks.addAll(cardGame.getCardStacks(localId, SolShowCardStackType.LAYDOWN));
                    allLaydownStacks.addAll(cardGame.getCardStacks(remoteId, SolShowCardStackType.LAYDOWN));
                    for (E_CardStack dstCardStack : allLaydownStacks)
                    {
                        if (canMove(initiatorStack, dstCardStack, card))
                        {
                            allPossibleMoves.add(new GameMove(Operation.Move, initiatorStack, dstCardStack, card));
                        }
                    }
                }
                else 
                {
                    allPossibleMoves.add(new GameMove(Operation.ChangeVisibility, initiatorStack, null, card));
                }
            }            
        }
        else if (cardStackType.equals(SolShowCardStackType.LAYDOWN))
        {
            // no use moves, the cards in these stacks are in their final position
        }
        else
        {
            throw new UnsupportedOperationException("No such stack type: " + cardStackType);            
        }
        return allPossibleMoves;
    }

    @Override
    public void move(E_CardStack srcCardStack, E_CardStack dstCardStack, E_Card card) {
        Preconditions.checkArgument(canMove(srcCardStack, dstCardStack, card));
        
        UUID localId = Services.get(IContextProvider.class).getThreadContext().getLocalId();
        
        boolean depotInvolved = srcCardStack.getCardStackType().equals(SolShowCardStackType.DEPOT) || 
                            dstCardStack.getCardStackType().equals(SolShowCardStackType.DEPOT);
        
        boolean flipOrder = depotInvolved;
                
        // request major nr before creating commands
        int major = Services.get(IContextProvider.class).getThreadContext().getCardGameState().history.newCommandMajorId();
        MetaInfo fullCmdMetaInfo = new MetaInfo(major);
        
        C_Move moveCmd = new C_Move(srcCardStack.getCardStackId(), dstCardStack.getCardStackId(), card.getCardId(), flipOrder); 
        
        ICommand fullCmd;
        if (depotInvolved)
        {
            C_SetVisible visCmd = new C_SetVisible(localId, srcCardStack.getCardsFrom(card), !card.isVisible());
            fullCmd = new C_Composite(moveCmd, visCmd);
        }
        else 
        {
            fullCmd = moveCmd;
        }
        
        getCommandProcessor().schedule(fullCmd);
    }

    @Override
    public void use(E_CardStack initiatorStack) {
        UUID localId = Services.get(IContextProvider.class).getThreadContext().getLocalId();
        
        List<GameMove> listOfMoves = getUseMoves(initiatorStack);
        Preconditions.checkArgument(!listOfMoves.isEmpty());
                    
        // for now we just take the first possibility
        GameMove move = listOfMoves.get(0);
        
        if (move.operation == Operation.Move)
        {
            move(move.srcCardStack, move.dstCardStack, move.card);
        }
        else if (move.operation == Operation.ChangeVisibility)
        {
            C_SetVisible cmd = new C_SetVisible(localId, Arrays.asList(move.card), true);
            getCommandProcessor().schedule(cmd);
        } 
    }
    
    @Override
    public void createGame(CreateServerInfo srvinfo)
    {
        Services.get(IContextProvider.class).installContext(ContextType.Server, UUID.randomUUID(), srvinfo.srvname);
        C_CreateGame cmd = new C_CreateGame(srvinfo);
        Services.get(IContextProvider.class).getContext(ContextType.Server).commandProcessor.schedule(cmd);
    }
    
    @Override
    public boolean canUndo() 
    {
        return false;
    }
    
    @Override
    public boolean canRedo() 
    {
        return false;
    }
}
