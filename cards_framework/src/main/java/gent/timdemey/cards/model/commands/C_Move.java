package gent.timdemey.cards.model.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ACommand;
import gent.timdemey.cards.readonlymodel.CommandType;
import gent.timdemey.cards.readonlymodel.IGameEventListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;

public class C_Move extends CommandBase
{
    public final UUID srcCardStackId;
    public final UUID dstCardStackId;
    public final UUID cardId;
    public final boolean flipOrder; 
        
    private List<ReadOnlyCard> transferCards;
    private List<ReadOnlyCard> flippedTransferCards;
   
    C_Move (UUID srcCardStackId, UUID dstCardStackId, UUID cardId, boolean flipOrder)
    {
        this.srcCardStackId = srcCardStackId;
        this.dstCardStackId = dstCardStackId;
        this.cardId = cardId;
        this.flipOrder = flipOrder;
    }
    
    @Override
    public boolean canExecute() {
        ReadOnlyCardGame cardGame = getThreadContext().getCardGameState().cardGame;
        
        ReadOnlyCardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        return srcCardStack.getCards().contains(cardId);        
    }
    
    @Override
    public void execute() 
    {
        ReadOnlyCardGame cardGame = getThreadContext().getCardGameState().cardGame;
        ReadOnlyCardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        ReadOnlyCardStack dstCardStack = cardGame.getCardStack(dstCardStackId);
        
        if (transferCards == null)
        {
            List<ReadOnlyCard> cards = srcCardStack.getCards();
            ReadOnlyCard card = srcCardStack.getCard(cardId);
            transferCards = new ArrayList<>(cards.subList(cards.indexOf(card), cards.size()));
            
            if (flipOrder)
            {
                flippedTransferCards = new ArrayList<>(transferCards);
                Collections.reverse(flippedTransferCards);
            }
        }
        
        srcCardStack.removeAll(transferCards);
        
        if (flipOrder)
        {
            dstCardStack.addAll(flippedTransferCards);
        }
        else
        {
            dstCardStack.addAll(transferCards);
        }
        
        transferCards.forEach(card -> card.setCardStack(dstCardStack));    
        
        
    }
    
    @Override
    public boolean canUndo() {
        ReadOnlyCardStack dstCardStack = getThreadContext().getCardGameState().cardGame.getCardStack(dstCardStackId);
        
        return dstCardStack.contains(cardId);
    }

    @Override
    public void undo() 
    {
        ReadOnlyCardGame cardGame = getThreadContext().getCardGameState().cardGame;
        ReadOnlyCardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        ReadOnlyCardStack dstCardStack = cardGame.getCardStack(dstCardStackId);
        
        // for removal, it doesn't really mather which list we take
        if (flipOrder)
        {
            dstCardStack.removeAll(flippedTransferCards);
        }
        else
        {
            dstCardStack.removeAll(transferCards);
        } 
        
        srcCardStack.addAll(transferCards);
        transferCards.forEach(card -> card.setCardStack(srcCardStack));    
    }
}
