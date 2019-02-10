package gent.timdemey.cards.services.gamepanel;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.E_Card;
import gent.timdemey.cards.entities.E_CardGame;
import gent.timdemey.cards.entities.E_CardStack;
import gent.timdemey.cards.entities.IContextProvider;
import gent.timdemey.cards.entities.IGameOperations;
import gent.timdemey.cards.services.plugin.IPositionManager;
import gent.timdemey.cards.services.scaleman.IScalableImageManager;
import gent.timdemey.cards.services.scaleman.JScalableImage;

class CardDragListener extends MouseAdapter {
    
    private static class CardDragState
    {        
        private final int xstart;
        private final int ystart;
        
        private CardDragState(int xstart, int ystart)
        {
            this.xstart = xstart;
            this.ystart = ystart;
        }
    }
    
    private int mouse_xstart;
    private int mouse_ystart;
        
    private final List<CardDragState> dragStates;
    private final List<JScalableImage> draggedImages;
    
    CardDragListener ()
    {
        dragStates = new ArrayList<>();
        draggedImages = new ArrayList<>();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        
        if (!dragStates.isEmpty())
        {
            int mouse_x = e.getX();
            int mouse_y = e.getY();

            int dx = mouse_x - mouse_xstart;
            int dy = mouse_y - mouse_ystart;
            
            for (int i = 0; i < dragStates.size(); i++)
            {
                CardDragState state = dragStates.get(i);
                JScalableImage scaleImg = draggedImages.get(i);
                int card_x = state.xstart + dx;
                int card_y = state.ystart + dy;
                
                scaleImg.setLocation(card_x, card_y);
            }  
            
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        GamePanel gpMan = (GamePanel) e.getComponent();
        
        Component comp = gpMan.getComponentAt(e.getPoint());
        if (!(comp instanceof JScalableImage))
        {
            return;
        }
        
        JScalableImage jscalable = (JScalableImage) comp;        
        UUID id = Services.get(IScalableImageManager.class).getUUID(jscalable);
        E_CardGame cardGame = Services.get(IContextProvider.class).getThreadContext().getCardGameState().getCardGame();
        
        
        if (cardGame.isCard(id))
        {
            E_Card card = cardGame.getCard(id);
            E_CardStack stack = card.getCardStack();
            
            boolean pullable = Services.get(IGameOperations.class).canPull(stack, card);
            boolean useable = stack.getHighestCard() == card && Services.get(IGameOperations.class).canUse(stack);
            
            boolean pull = pullable && (!useable || useable &&  e.getClickCount() % 2 == 1);
            boolean use = useable && (!pullable || pullable &&  e.getClickCount() % 2 == 0);
                            
            if (pull)
            {
                List<E_Card> cards = stack.getCardsFrom(card);                    
                
                dragStates.clear(); // ensure list is empty (might miss mouseReleased event)
                draggedImages.clear();
                
                for (int i = 0 ; i < cards.size(); i++)
                {
                    E_Card currentCard = cards.get(i);
                    JScalableImage currentJcard = Services.get(IScalableImageManager.class).getJScalableImage(currentCard.getCardId());
                    int card_xstart = currentJcard.getLocation().x;
                    int card_ystart = currentJcard.getLocation().y;
                    
                    ((GamePanel)currentJcard.getParent()).setLayer(currentJcard, 10000 + i);
                    
                    CardDragState dragState = new CardDragState(card_xstart, card_ystart);
                    dragStates.add(dragState);
                    draggedImages.add(currentJcard);
                }
            }
            else if (use)
            {
                Services.get(IGameOperations.class).use(stack);
            }
            
            mouse_xstart = e.getX();
            mouse_ystart = e.getY();
                        
        }
        else if (cardGame.isCardStack(id))
        {
            E_CardStack cardStack = Services.get(IPositionManager.class).getCardStackAt(e.getPoint());
            if (cardStack != null)
            {
                if (Services.get(IGameOperations.class).canUse(cardStack))
                {
                    Services.get(IGameOperations.class).use(cardStack);
                }
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        
        E_CardGame cardGame = Services.get(IContextProvider.class).getThreadContext().getCardGameState().getCardGame();
        
        if (!draggedImages.isEmpty())       
        {
            // check if first card is over another card or an empty card stack
            boolean pushed = false;
            
            JScalableImage rootJCard = draggedImages.get(0);
                       
            for (Component comp : rootJCard.getParent().getComponents()) // inefficient?
            {
                if (!(comp instanceof JScalableImage))
                {
                    continue;
                }
                JScalableImage otherScalable = (JScalableImage) comp;
                if (draggedImages.contains(otherScalable))
                {
                    continue;
                }
                if (!rootJCard.getBounds().intersects(otherScalable.getBounds()))
                {
                    continue;
                }
                
                // intersection with a JScalableImage
                UUID id = Services.get(IScalableImageManager.class).getUUID(otherScalable);
                E_CardStack cardStack;
                if (cardGame.isCard(id))
                {
                    cardStack = cardGame.getCard(id).getCardStack();
                }
                else if (cardGame.isCardStack(id))
                {
                    cardStack = cardGame.getCardStack(id);
                }
                else
                {
                    continue;
                }
                                
                List<E_Card> cards = draggedImages.stream()
                        .map(jcard -> Services.get(IScalableImageManager.class).getUUID(jcard))
                        .map(cardId -> cardGame.getCard(cardId))
                        .collect(Collectors.toList());
                if (Services.get(IGameOperations.class).canPush(cardStack, cards))
                {
                    Services.get(IGameOperations.class).move(cards.get(0).getCardStack(), cardStack, cards.get(0));
                    pushed = true;
                    break;
                }                    
            }
         /*   if (!pushed)
            {
                // try empty card stacks below the dragged card 
                List<CardStack> cardStacks = Services.get(IPositionManager.class).getCardStacksIn(rootJCard.getBounds());
                List<Card> cards = draggedImages.stream()
                        .map(jcard -> (Card) Services.get(IScalableImageManager.class).getManagedObject(jcard))
                        .collect(Collectors.toList());
                for (CardStack cardStack : cardStacks)
                {
                    if (!cardStack.isEmpty())
                    {
                        continue;
                    }
                    if (Services.get(IGameOperations.class).canPush(cardStack, cards))
                    {
                        Services.get(IGameOperations.class).move(cards.get(0).getCardStack(), cardStack, cards.get(0));
                        pushed = true;
                        break;
                    }
                }
            }*/
            if (!pushed)
            {
                for (int i = 0; i < draggedImages.size(); i++)
                {
                    JScalableImage scaledImg = draggedImages.get(i);
                    
                    UUID id = Services.get(IScalableImageManager.class).getUUID(scaledImg);
                    E_Card card = cardGame.getCard(id);
                    Services.get(IGamePanelManager.class).animatePosition(card);
                }
            }
            
            draggedImages.clear();
            dragStates.clear();
        }
    }
}

