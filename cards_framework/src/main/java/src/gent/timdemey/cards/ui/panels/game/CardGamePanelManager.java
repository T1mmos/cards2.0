package gent.timdemey.cards.ui.panels.game;

import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityBase;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.ui.components.SImageResource;
import gent.timdemey.cards.ui.components.drawers.GamePanelDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.drawers.ScaledImageDrawer;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import gent.timdemey.cards.ui.components.swing.JSFactory;
import gent.timdemey.cards.ui.components.swing.JSImage;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.PanelManagerBase;

public class CardGamePanelManager extends PanelManagerBase
{
    private JSLayeredPane gamePanel;
    private CardGamePanelMouseListener mouseListener;
    private CardGamePanelStateListener stateListener;

    @Override
    public void preload()
    {
        preloadImages();
        preloadFonts();
    }

    @Override
    public void onShown()
    {
        gamePanel.addMouseMotionListener(mouseListener);
        gamePanel.addMouseListener(mouseListener);
        Services.get(IContextService.class).getThreadContext().addStateListener(stateListener);
    }

    @Override
    public void onHidden()
    {
        gamePanel.addMouseMotionListener(mouseListener);
        gamePanel.removeMouseListener(mouseListener);
        Services.get(IContextService.class).getThreadContext().removeStateListener(stateListener);
    }

    @Override
    public JSLayeredPane createPanel()
    {
        gamePanel = JSFactory.createLayeredPane(ComponentTypes.PANEL, new GamePanelDrawer());
        gamePanel.setLayout(null);
        mouseListener = createMouseListener();
        stateListener = createStateListener();
        gamePanel.addContainerListener(new CardGamePanelContainerListener());
        
        return gamePanel;
    }
    
    protected CardGamePanelMouseListener createMouseListener()
    {
        return new CardGamePanelMouseListener();
    }

    protected CardGamePanelStateListener createStateListener()
    {
        return new CardGamePanelStateListener();
    }
    
    @Override
    public JSLayeredPane getPanel()
    {
        return gamePanel;
    }

    @Override
    public void addComponentCreators(List<Runnable> compCreators)
    {
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();      
        
        List<ReadOnlyCard> cards = cardGame.getCards();
        for (int i = 0; i < cards.size(); i++)
        {
            ReadOnlyCard card = cards.get(i);
            compCreators.add(() -> createJSImage(card));
        }       
    }

    @Override
    public void updateComponent(JComponent comp)
    {
        ComponentType compType = ((IHasComponent<?>) comp).getComponent().getComponentType();
        if (compType.hasTypeName(ComponentTypes.CARD))
        {
            JSImage jsimage_card = (JSImage) comp;
            ReadOnlyCard card = (ReadOnlyCard) jsimage_card.getComponent().getPayload();

            IIdService idServ = Services.get(IIdService.class);
            UUID resId = card.isVisible() ? idServ.createCardFrontScalableResourceId(card.getSuit(), card.getValue())
                    : idServ.createCardBackScalableResourceId();
            ((ScaledImageDrawer) jsimage_card.getDrawer()).setScalableImageResource(resId);
            jsimage_card.repaint();

            return;
        }

        if (compType.hasTypeName(ComponentTypes.CARDSTACK))
        {
            JSImage jsimage_cs = (JSImage) comp;
            ReadOnlyCardStack cardStack = (ReadOnlyCardStack) jsimage_cs.getComponent().getPayload();

            IIdService idServ = Services.get(IIdService.class);
            UUID resId = idServ.createCardStackScalableResourceId(cardStack.getCardStackType());
            ((ScaledImageDrawer) jsimage_cs.getDrawer()).setScalableImageResource(resId);
            return;
        }
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
        IStateListener stateListener = Services.get(IStateListener.class);
        Services.get(IContextService.class).getThreadContext().removeStateListener(stateListener);

        gamePanel.removeAll();
        clearComponents();

        mouseListener = null;
        gamePanel = null;
    }

    @Override
    public void positionComponent(JComponent comp)
    {
        IPositionService posServ = Services.get(IPositionService.class);
        LayeredArea layArea = posServ.getLayeredArea(comp);
        ((IHasComponent<?>) comp).getComponent().setAbsCoords(layArea.abscoords_src);
        ((IHasDrawer) comp).getDrawer().setMirror(layArea.mirror);        
        
        ((JSLayeredPane) comp.getParent()).setLayer(comp, layArea.endLayer);
    }

    public void addRescaleRequests(List<? super RescaleRequest> requests)
    {
        IIdService idServ = Services.get(IIdService.class);
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState()
                .getCardGame();

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
    
    protected void createJSImage(ReadOnlyCard card)
    {
        IIdService uuidServ = Services.get(IIdService.class);
        IScalingService scaleServ = Services.get(IScalingService.class);

        UUID compId = createComponentId(card);

        JComponent comp = comp2jcomp.get(compId);
        if (comp != null)
        {
            throw new IllegalArgumentException("A scalable component already exist for the given model object: " + card);            
        }
        
        UUID resFrontId = uuidServ.createCardFrontScalableResourceId(card.getSuit(), card.getValue());
        UUID resBackId = uuidServ.createCardBackScalableResourceId();

        // create the component using its necessary image resources
        SImageResource res_front = (SImageResource) scaleServ.getSResource(resFrontId);
        SImageResource res_back = (SImageResource) scaleServ.getSResource(resBackId);
        
        createJSImage(compId, ComponentTypes.CARD, card, res_front, res_back);
    }
    
    protected JSImage getJSImage(ReadOnlyCard card)
    {
        return (JSImage) getComponent((ReadOnlyEntityBase<?>) card);
    }
        
    protected void createJSImage(ReadOnlyCardStack cardstack)
    {
        IIdService idServ = Services.get(IIdService.class);

        UUID compId = createComponentId(cardstack);

        JComponent comp = comp2jcomp.get(compId);
        if (comp != null)
        {
            throw new IllegalArgumentException("A scalable component already exist for the given model object: " + cardstack);            
        }
        
        UUID csResId = idServ.createCardStackScalableResourceId(cardstack.getCardStackType());

        // create the component using its necessary image resources
        IScalingService scaleServ = Services.get(IScalingService.class);
        SImageResource res = (SImageResource) scaleServ.getSResource(csResId);
        createJSImage(compId, ComponentTypes.CARDSTACK, cardstack, res);
    }
    
    protected JSImage getJSImage(ReadOnlyCardStack cardStack)
    {
        return (JSImage) getComponent(cardStack);
    }
    
}
