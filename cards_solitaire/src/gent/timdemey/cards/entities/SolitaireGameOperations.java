package gent.timdemey.cards.entities;


import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.GameMove.Operation;

/**
 * Atomic game operations. All game operations that alter the state of the game must be 
 * built on top of these. (Atomic operations are serialized and sent to the server).
 * @author Timmos
 */
public class SolitaireGameOperations extends AGameOperations {
    
    
    ////////////////////////
    /// "Can" operations /// 
    ////////////////////////
            
    @Override
    public List<GameMove> getUseMoves(E_CardStack initiatorStack)    
    {
        E_CardGame cardGame = getCardGame();
        
        List<GameMove> allPossibleMoves = new ArrayList<>();
        String cardStackType = initiatorStack.getCardStackType();
        UUID localId = Services.get(IContextProvider.class).getThreadContext().getLocalId();
        
        if (cardStackType.equals(SolitaireCardStackType.DEPOT))
        {
            E_CardStack otherCardStack = cardGame.getCardStack(localId, SolitaireCardStackType.TURNOVER, 0);  
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
        if (cardStackType.equals(SolitaireCardStackType.TURNOVER) || cardStackType.equals(SolitaireCardStackType.MIDDLE))
        {
            if (!initiatorStack.isEmpty())
            {
                E_Card card = initiatorStack.getHighestCard();
                if (card.isVisible())
                {
                    for (E_CardStack dstCardStack : cardGame.getCardStacks(localId, SolitaireCardStackType.LAYDOWN))
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
        return allPossibleMoves;
            
      
    }
        
    @Override
    public boolean canPull (E_CardStack srcCardStack, E_Card card)
    {
        String srcType = srcCardStack.getCardStackType();
        List<E_Card> cards = srcCardStack.getCards();        

        Preconditions.checkArgument(cards.contains(card));
        
        if (!card.isVisible())
        {
            return false;
        }
        
        if (srcType.equals(SolitaireCardStackType.TURNOVER) || srcType.equals(SolitaireCardStackType.LAYDOWN))
        {
            return cards.get(cards.size() - 1) == card; // 1 card
        }
        else if (srcType.equals(SolitaireCardStackType.MIDDLE))
        {
            int cardIndex = cards.indexOf(card);
            for (int i = cardIndex; i < cards.size() - 1; i++)
            {
                E_Card lowerCard = cards.get(i);
                E_Card higherCard = cards.get(i+1);
               
                if (!higherCard.isVisible())
                {
                    return false;
                }
                
                if (lowerCard.getCardColor() == higherCard.getCardColor())
                {
                    return false;
                }
                
                if (lowerCard.getCardValue().getOrderAtoK() != higherCard.getCardValue().getOrderAtoK() + 1)
                {
                    return false;
                }
            }
            
            return true;            
        }
        
        
        return false;
    }
    
    @Override
    public boolean canPush (E_CardStack dstCardStack, List<E_Card> srcCards)
    {
        Preconditions.checkNotNull(dstCardStack);
        Preconditions.checkNotNull(srcCards);
        Preconditions.checkArgument(!srcCards.isEmpty());
        
        String dstType = dstCardStack.getCardStackType();
        
        if (dstType.equals(SolitaireCardStackType.MIDDLE))
        {
            if (dstCardStack.isEmpty() 
                   && srcCards.get(0).getCardValue() == E_CardValue.V_K
                   ||
               !dstCardStack.isEmpty()  
                   && dstCardStack.getHighestCard().getCardColor() != srcCards.get(0).getCardColor()
                   && dstCardStack.getHighestCard().getCardValue().getOrderAtoK() == srcCards.get(0).getCardValue().getOrderAtoK() + 1
               )
            {
                return true;    
            }            
        }
        else if (dstType.equals(SolitaireCardStackType.LAYDOWN))
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
    public boolean canMove (E_CardStack srcCardStack, E_CardStack dstCardStack, E_Card card)
    {
        Preconditions.checkArgument(srcCardStack.getCards().contains(card));
        
        List<E_Card> toTransfer = srcCardStack.getCardsFrom(card);
        if (canPull(srcCardStack, card) && canPush(dstCardStack, toTransfer))   // user action
        {
            return true;
        }
        else                                                                    // game logic action
        {
            String srcCardStackType = srcCardStack.getCardStackType();
            String dstCardStackType = dstCardStack.getCardStackType();
            if (srcCardStackType.equals(SolitaireCardStackType.DEPOT) && dstCardStackType.equals(SolitaireCardStackType.TURNOVER))
            {
                return !srcCardStack.isEmpty() && srcCardStack.getHighestCard() == card;
            }  
            else if (srcCardStackType.equals(SolitaireCardStackType.TURNOVER) && dstCardStackType.equals(SolitaireCardStackType.DEPOT))
            {
                return !srcCardStack.isEmpty() && dstCardStack.isEmpty() && srcCardStack.getLowestCard() == card;
            }
        }
        return false;
    }
    
    @Override
    public void move (E_CardStack srcCardStack, E_CardStack dstCardStack, E_Card card)
    {
        Preconditions.checkArgument(canMove(srcCardStack, dstCardStack, card));
        
        UUID localId = Services.get(IContextProvider.class).getThreadContext().getLocalId();
        
        boolean depotInvolved = srcCardStack.getCardStackType().equals(SolitaireCardStackType.DEPOT) || 
                            dstCardStack.getCardStackType().equals(SolitaireCardStackType.DEPOT);
        
        boolean flipOrder = depotInvolved;
                
        // request major nr before creating commands
        int major = Services.get(IContextProvider.class).getThreadContext().getCardGameState().history.newCommandMajorId();
        
        C_Move moveCmd = new C_Move( srcCardStack.getCardStackId(), dstCardStack.getCardStackId(), card.getCardId(), flipOrder); 
        
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
    public void use (E_CardStack initiatorStack)
    {
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
    public void joinGame(InetAddress srvInetAddress, int tcpport, String playerName) 
    {
        throw new UnsupportedOperationException();
    }
}
