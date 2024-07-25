package gent.timdemey.cards.ui.panels.game;

import gent.timdemey.cards.di.Container;
import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.model.entities.state.CardSuit;
import gent.timdemey.cards.model.entities.state.CardValue;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyEntityBase;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceNameService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.ui.components.SImageResource;
import gent.timdemey.cards.ui.components.drawers.GamePanelDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.drawers.ScaledImageDrawer;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import gent.timdemey.cards.ui.components.swing.JSImage;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.PanelManagerBase;

public class CardGamePanelManager extends PanelManagerBase
{
    private JSLayeredPane gamePanel;
    private CardGamePanelMouseListener mouseListener;
    private CardGamePanelStateListener stateListener;
    private CardGamePanelContainerListener contListener;
    private final IContextService _ContextService;
    private final IResourceNameService _ResourceNameService;

    public CardGamePanelManager(
            Container container,
            IResourceNameService resourceNameService,
            IContextService contextService)
    {
        super(container);
        
        this._ResourceNameService = resourceNameService;
        this._ContextService = contextService;
    }
    
    @Override
    public void preload()
    {
        preloadImages();
        preloadFonts();
    }

    @Override
    public void onShown()
    {
        Context ctxt = _ContextService.getThreadContext();
        
        gamePanel.addMouseMotionListener(mouseListener);
        gamePanel.addMouseListener(mouseListener);
        ctxt.addStateListener(stateListener);
    }

    @Override
    public void onHidden()
    {
        Context ctxt = _ContextService.getThreadContext();
        
        gamePanel.addMouseMotionListener(mouseListener);
        gamePanel.removeMouseListener(mouseListener);
        ctxt.removeStateListener(stateListener);
    }

    @Override
    public JSLayeredPane createPanel()
    {
        gamePanel = _JSFactory.createLayeredPane(ComponentTypes.PANEL, _Container.Get(GamePanelDrawer.class));
        gamePanel.setLayout(null);
        
        mouseListener = createMouseListener();
        stateListener = createStateListener();
        contListener = createContainerListener();
        
        gamePanel.addContainerListener(contListener);
        
        return gamePanel;
    }

    @Override
    public void destroyPanel()
    {
        super.destroyPanel();
        
        gamePanel.removeContainerListener(contListener);
        gamePanel.removeAll();
        
        mouseListener = null;
        stateListener = null;
        contListener = null;
        
        gamePanel = null;
    }
    
    protected CardGamePanelMouseListener createMouseListener()
    {
        return _Container.Get(CardGamePanelMouseListener.class);
    }

    protected CardGamePanelStateListener createStateListener()
    {
        return _Container.Get(CardGamePanelStateListener.class);
    }
    
    protected CardGamePanelContainerListener createContainerListener()
    {
        return _Container.Get(CardGamePanelContainerListener.class);
    }    
    
    @Override
    public JSLayeredPane getPanel()
    {
        return gamePanel;
    }

    @Override
    public void addComponentCreators(List<Runnable> compCreators)
    {
        ReadOnlyCardGame cardGame = _ContextService.getThreadContext().getReadOnlyState().getCardGame();      
        
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

            UUID resId = card.isVisible() ? _IdService.createCardFrontScalableResourceId(card.getSuit(), card.getValue())
                    : _IdService.createCardBackScalableResourceId();
            ((ScaledImageDrawer) jsimage_card.getDrawer()).setScalableImageResource(resId);
            jsimage_card.repaint();

            return;
        }

        if (compType.hasTypeName(ComponentTypes.CARDSTACK))
        {
            JSImage jsimage_cs = (JSImage) comp;
            ReadOnlyCardStack cardStack = (ReadOnlyCardStack) jsimage_cs.getComponent().getPayload();

            UUID resId = _IdService.createCardStackScalableResourceId(cardStack.getCardStackType());
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
        // card back
        preloadImage(_IdService.createCardBackScalableResourceId(), _ResourceNameService.getFilePath(ResourceDescriptors.CardBack));

        // card fronts
        for (CardSuit suit : CardSuit.values())
        {
            for (CardValue value : CardValue.values()) // have fun reading the code lol
            {
                UUID resId = _IdService.createCardFrontScalableResourceId(suit, value);
                preloadImage(resId, _ResourceNameService.getFilePath(ResourceDescriptors.CardFront, suit, value));
            }
        }
    }

    protected void preloadFonts()
    {
    }

    @Override
    public void positionComponent(JComponent comp)
    {
        LayeredArea layArea = _PositionService.getLayeredArea(comp);
        ((IHasComponent<?>) comp).getComponent().setAbsCoords(layArea.abscoords_src);
        ((IHasDrawer) comp).getDrawer().setMirror(layArea.mirror);        
        
        ((JSLayeredPane) comp.getParent()).setLayer(comp, layArea.endLayer);
    }

    public void addRescaleRequests(List<? super RescaleRequest> requests)
    {
        ReadOnlyCardGame cardGame = _ContextService.getThreadContext().getReadOnlyState()
                .getCardGame();

        // cards - fronts
        for (ReadOnlyCard card : cardGame.getCards())
        {
            UUID resId = _IdService.createCardFrontScalableResourceId(card.getSuit(), card.getValue());
            addRescaleRequest(requests, ComponentTypes.CARD, resId);
        }
        // cards - back
        {
            UUID resId = _IdService.createCardBackScalableResourceId();
            addRescaleRequest(requests, ComponentTypes.CARD, resId);
        }
        // card stacks
        for (ReadOnlyCardStack cs : cardGame.getCardStacks())
        {
            String csType = cs.getCardStackType();
            ComponentType cd_cardstack = ComponentTypes.CARDSTACK.derive(csType);
            UUID resId = _IdService.createCardStackScalableResourceId(csType);
            addRescaleRequest(requests, cd_cardstack, resId);
        }
    }   
    
    protected void createJSImage(ReadOnlyCard card)
    {
        UUID compId = createComponentId(card);

        JComponent comp = comp2jcomp.get(compId);
        if (comp != null)
        {
            throw new IllegalArgumentException("A scalable component already exist for the given model object: " + card);            
        }
        
        UUID resFrontId = _IdService.createCardFrontScalableResourceId(card.getSuit(), card.getValue());
        UUID resBackId = _IdService.createCardBackScalableResourceId();

        // create the component using its necessary image resources
        SImageResource res_front = (SImageResource) _ScalingService.getSResource(resFrontId);
        SImageResource res_back = (SImageResource) _ScalingService.getSResource(resBackId);
        
        createJSImage(compId, ComponentTypes.CARD, card, res_front, res_back);
    }
    
    protected JSImage getJSImage(ReadOnlyCard card)
    {
        return (JSImage) getComponent((ReadOnlyEntityBase<?>) card);
    }
        
    protected void createJSImage(ReadOnlyCardStack cardstack)
    {
        UUID compId = createComponentId(cardstack);

        JComponent comp = comp2jcomp.get(compId);
        if (comp != null)
        {
            throw new IllegalArgumentException("A scalable component already exist for the given model object: " + cardstack);            
        }
        
        UUID csResId = _IdService.createCardStackScalableResourceId(cardstack.getCardStackType());

        // create the component using its necessary image resources
        SImageResource res = (SImageResource) _ScalingService.getSResource(csResId);
        createJSImage(compId, ComponentTypes.CARDSTACK, cardstack, res);
    }
    
    protected JSImage getJSImage(ReadOnlyCardStack cardStack)
    {
        return (JSImage) getComponent(cardStack);
    }
    
}
