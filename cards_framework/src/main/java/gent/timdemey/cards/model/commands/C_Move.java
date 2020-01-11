package gent.timdemey.cards.model.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.model.cards.Card;
import gent.timdemey.cards.model.cards.CardGame;
import gent.timdemey.cards.model.cards.CardStack;
import gent.timdemey.cards.model.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;

public class C_Move extends CommandBase
{
    public final UUID srcCardStackId;
    public final UUID dstCardStackId;
    public final UUID cardId;
    public final boolean flipOrder; 
        
    private List<Card> transferCards;
    private List<Card> flippedTransferCards;
   
    C_Move (UUID srcCardStackId, UUID dstCardStackId, UUID cardId, boolean flipOrder)
    {
        this.srcCardStackId = srcCardStackId;
        this.dstCardStackId = dstCardStackId;
        this.cardId = cardId;
        this.flipOrder = flipOrder;
    }
    @Override
    protected boolean canExecute(Context context, ContextType type, State state)
    {
        CardStack srcCardStack = state.getCardGame().getCardStacks().get(srcCardStackId);
        return srcCardStack.getCards().contains(cardId);     
    }
    
    @Override
    protected void execute(Context context, ContextType type, State state)
    {
    	CardGame cardGame = state.getCardGame();
        CardStack srcCardStack = cardGame.getCardStacks().get(srcCardStackId);
        CardStack dstCardStack = cardGame.getCardStacks().get(dstCardStackId);
        
        if (transferCards == null)
        {
            List<Card> cards = srcCardStack.getCards();
            Card card = srcCardStack.getCards().get(cardId);
            transferCards = new ArrayList<>(cards.subList(cards.indexOf(card), cards.size()));
            
            if (flipOrder)
            {
                flippedTransferCards = new ArrayList<>(transferCards);
                Collections.reverse(flippedTransferCards);
            }
        }
        
        srcCardStack.getCards().removeAll(transferCards);
        
        if (flipOrder)
        {
            dstCardStack.addAll(flippedTransferCards);
        }
        else
        {
            dstCardStack.addAll(transferCards);
        }
        
        transferCards.forEach(card -> card.cardStackRef.set(dstCardStack));   
        
        if (type == ContextType.Server)
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
        
        // for removal, it doesn't really mather which list we take
        if (flipOrder)
        {
            dstCardStack.getCards().removeAll(flippedTransferCards);
        }
        else
        {
            dstCardStack.getCards().removeAll(transferCards);
        } 
        
        srcCardStack.addAll(transferCards);
        transferCards.forEach(card -> card.cardStackRef.set(srcCardStack));    
    }
}
