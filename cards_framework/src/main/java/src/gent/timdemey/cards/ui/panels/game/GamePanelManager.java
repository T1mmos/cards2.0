package gent.timdemey.cards.ui.panels.game;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.animation.PanelAnimator;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.ui.components.drawers.GamePanelDrawer;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.components.swing.JSFactory;
import gent.timdemey.cards.ui.components.swing.JSImage;
import gent.timdemey.cards.ui.panels.PanelManagerBase;

public class GamePanelManager extends PanelManagerBase
{
    private JSLayeredPane gamePanel;
    private PanelAnimator animator;
    private GamePanelMouseListener dragListener;
        
    @Override
    public void preload()
    {
        preloadImages();
        preloadFonts();
    }
    
    @Override
    public void onShown()
    {
        gamePanel.addMouseMotionListener(dragListener);
        gamePanel.addMouseListener(dragListener);
        gamePanel.setVisible(true);
        gamePanel.repaint();      
    }    

    @Override
    public void onHidden()
    {
        gamePanel.setVisible(false);
        gamePanel.removeMouseListener(dragListener);
        gamePanel.removeMouseListener(dragListener);
    }
    
    @Override
    public JSLayeredPane createPanel()
    {
        gamePanel = JSFactory.createLayeredPane(ComponentTypes.PANEL, new GamePanelDrawer());
        dragListener = new GamePanelMouseListener(); 
        animator = new PanelAnimator();           

        IStateListener stateListener = Services.get(IStateListener.class);
        Services.get(IContextService.class).getThreadContext().addStateListener(stateListener);
        animator.start();
        
        return gamePanel;
    }
    
    @Override
    public JSLayeredPane getPanel()
    {
        return gamePanel;
    }
    
    @Override
    public void createComponents()
    {
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();

        // card components
        List<ReadOnlyCard> cards = cardGame.getCards();
        for (int i = 0; i < cards.size(); i++)
        {
            ReadOnlyCard card = cards.get(i);
            JSImage jsimage = scaleCompServ.createImage(card, this);
            add(jsimage);
            updateComponent(jsimage);
        }
    }    

    @Override
    public void updateComponent(JComponent comp)
    {
        ComponentType compType = comp.getComponentType();
        if(compType.hasTypeName(ComponentTypes.CARD))
        {
            JSImage cardComp = (JSImage) comp;
            ReadOnlyCard card = (ReadOnlyCard) cardComp.getPayload();

            IIdService idServ = Services.get(IIdService.class);
            UUID resId = card.isVisible() ? idServ.createCardFrontScalableResourceId(card.getSuit(), card.getValue())
                : idServ.createCardBackScalableResourceId();
            cardComp.setScalableImageResource(resId);
            cardComp.repaint();

            return;
        }

        if(compType.hasTypeName(ComponentTypes.CARDSTACK))
        {
            JSImage cardStackComp = (JSImage) comp;
            ReadOnlyCardStack cardStack = (ReadOnlyCardStack) cardStackComp.getPayload();

            IIdService idServ = Services.get(IIdService.class);
            UUID resId = idServ.createCardStackScalableResourceId(cardStack.getCardStackType());
            cardStackComp.setScalableImageResource(resId);
            return;
        }

        throw new UnsupportedOperationException("updateComponent not supported for ComponentType " + compType);
    }

    protected void preloadImages()
    {
        preloadCards();
    }
    
    protected void preloadCards()
    {
        IIdService idServ = Services.get(IIdService.class);
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);

        // card back
        preloadImage(idServ.createCardBackScalableResourceId(), resLocServ.getFilePath(ResourceDescriptors.CardBack));

        // card fronts
        for (Suit suit : Suit.values())
        {
            for (Value value : Value.values()) // have fun reading the code lol
            {
                UUID resId = idServ.createCardFrontScalableResourceId(suit, value);
                preloadImage(resId, resLocServ.getFilePath(ResourceDescriptors.CardFront, suit, value));
            }
        }
    }

    protected void preloadFonts()
    {
    }
    
    @Override
    public void destroyPanel()
    {
        animator.stop();

        IStateListener stateListener = Services.get(IStateListener.class);
        Services.get(IContextService.class).getThreadContext().removeStateListener(stateListener);
        
        gamePanel.removeAll();
        Services.get(IScalingService.class).clearComponentCache();
        
        dragListener = null;
        gamePanel = null;
    }

    protected int getNextAnimationLayer()
    {
        IPositionService posServ = Services.get(IPositionService.class);

        Optional<Integer> maxLayerInUse = animator.getScalableComponents().stream().map(sc -> getLayer(sc)).max(Integer::compare);
        int layer = posServ.getAnimationLayer();
        if(maxLayerInUse.isPresent())
        {
            layer = maxLayerInUse.get() + 1;
        }

        return layer;
    }
    
    public void createRescaleRequests(List<? super RescaleRequest> requests)
    {
        IIdService idServ = Services.get(IIdService.class);
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();

        // cards - fronts
        for (ReadOnlyCard card : cardGame.getCards())
        {
            UUID resId = idServ.createCardFrontScalableResourceId(card.getSuit(), card.getValue());
            addRescaleRequest(requests, ComponentTypes.CARD, resId);
        }
        // cards - back
        {
            UUID resId = idServ.createCardBackScalableResourceId();
            addRescaleRequest(requests, ComponentTypes.CARD, resId);
        }
        // card stacks
        for (ReadOnlyCardStack cs : cardGame.getCardStacks())
        {
            String csType = cs.getCardStackType();
            ComponentType cd_cardstack = ComponentTypes.CARDSTACK.derive(csType);
            UUID resId = idServ.createCardStackScalableResourceId(csType);
            addRescaleRequest(requests, cd_cardstack, resId);
        }
    }

    public void startAnimation(IComponent scaleComp)
    {
        int layer = getNextAnimationLayer();
        setLayer(scaleComp, layer);
        animator.animate(scaleComp);
    }

    public void stopAnimation(IComponent scaleComp)
    {
        animator.stopAnimate(scaleComp);
    }
    
    
}
