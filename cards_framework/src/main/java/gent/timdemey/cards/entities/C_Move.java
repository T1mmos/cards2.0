package gent.timdemey.cards.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

class C_Move extends ACommand {
    
    static class CompactConverter extends ASerializer<C_Move>
    {
        @Override
        protected void write(SerializationContext<C_Move> sc) {
            writeUUID(sc, PROPERTY_SRC_CARDSTACK_ID, sc.src.srcCardStackId);
            writeUUID(sc, PROPERTY_DST_CARDSTACK_ID, sc.src.dstCardStackId);
            writeUUID(sc, PROPERTY_CARD_ID, sc.src.cardId);
            writeBoolean(sc, PROPERTY_FLIPORDER, sc.src.flipOrder);
        }

        @Override
        protected C_Move read(DeserializationContext dc) {
            UUID srcCardStackId = readUUID(dc, PROPERTY_SRC_CARDSTACK_ID);
            UUID dstCardStackId = readUUID(dc, PROPERTY_DST_CARDSTACK_ID);
            UUID cardId = readUUID(dc, PROPERTY_CARD_ID);
            boolean flipOrder = readBoolean(dc, PROPERTY_FLIPORDER);
            
            return new C_Move(srcCardStackId, dstCardStackId, cardId, flipOrder);
        }        
    }
    
    public final UUID srcCardStackId;
    public final UUID dstCardStackId;
    public final UUID cardId;
    public final boolean flipOrder; 
        
    private List<E_Card> transferCards;
    private List<E_Card> flippedTransferCards;
   
    C_Move (UUID srcCardStackId, UUID dstCardStackId, UUID cardId, boolean flipOrder)
    {
        this.srcCardStackId = srcCardStackId;
        this.dstCardStackId = dstCardStackId;
        this.cardId = cardId;
        this.flipOrder = flipOrder;
    }
    
    @Override
    public CommandType getCommandType()
    {
        return CommandType.Gameplay;
    }    
    
    @Override
    public boolean canExecute() {
        E_CardGame cardGame = getThreadContext().getCardGameState().cardGame;
        
        E_CardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        return srcCardStack.getCards().contains(cardId);        
    }
    
    @Override
    public void execute() 
    {
        E_CardGame cardGame = getThreadContext().getCardGameState().cardGame;
        E_CardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        E_CardStack dstCardStack = cardGame.getCardStack(dstCardStackId);
        
        if (transferCards == null)
        {
            List<E_Card> cards = srcCardStack.getCards();
            E_Card card = srcCardStack.getCard(cardId);
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
        E_CardStack dstCardStack = getThreadContext().getCardGameState().cardGame.getCardStack(dstCardStackId);
        
        return dstCardStack.contains(cardId);
    }

    @Override
    public void undo() 
    {
        E_CardGame cardGame = getThreadContext().getCardGameState().cardGame;
        E_CardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        E_CardStack dstCardStack = cardGame.getCardStack(dstCardStackId);
        
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

    @Override
    public void visitExecuted(IGameEventListener listener) {
        E_CardGame cardGame = getThreadContext().getCardGameState().cardGame;
        E_CardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        E_CardStack dstCardStack = cardGame.getCardStack(dstCardStackId);        
        
        listener.onCardsMoved(srcCardStack, dstCardStack, transferCards);
    }

    @Override
    public void visitUndone(IGameEventListener listener) {
        E_CardGame cardGame = getThreadContext().getCardGameState().cardGame;
        E_CardStack srcCardStack = cardGame.getCardStack(srcCardStackId);
        E_CardStack dstCardStack = cardGame.getCardStack(dstCardStackId);
        
        listener.onCardsMoved(dstCardStack, srcCardStack, transferCards);
    }

}
