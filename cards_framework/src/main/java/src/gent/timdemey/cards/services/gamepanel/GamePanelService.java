package gent.timdemey.cards.services.gamepanel;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.contract.FontResource;
import gent.timdemey.cards.services.contract.ImageResource;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.ResourceUsage;
import gent.timdemey.cards.services.gamepanel.animations.GamePanelAnimator;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.IScalableResource;
import gent.timdemey.cards.services.scaling.img.ScalableImageComponent;
import gent.timdemey.cards.services.scaling.img.ScalableImageResource;
import gent.timdemey.cards.services.scaling.text.ScalableFontResource;

public class GamePanelService implements IGamePanelService
{
    private static final String FILEPATH_CARD_FRONTSIDE = "cards/edge_thick/%s_%s.png";
    private static final String FILEPATH_CARD_BACKSIDE = "backside_yellow.png";

    protected final GamePanelAnimator animator;

    private GamePanelResizeListener resizeListener;
    private GamePanelMouseListener dragListener;
    private IStateListener stateListener;
    private boolean drawDebug = false;
    protected GamePanel gamePanel;

    public GamePanelService()
    {
        this.animator = new GamePanelAnimator();
    }

    @Override
    public void preload()
    {
        preloadFonts();
        preloadCards();
    }

    protected void preloadImage(UUID resId, String path)
    {
        IResourceService resServ = Services.get(IResourceService.class);
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        ImageResource imgRes = resServ.getImage(path);
        IScalableResource<?> scaleRes_back = new ScalableImageResource(resId, imgRes);
        scaleCompServ.addScalableResource(scaleRes_back);
    }

    protected void preloadFont(UUID resId, String path)
    {
        IResourceService resServ = Services.get(IResourceService.class);
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        FontResource resp_font = resServ.getFont(path);

        IScalableResource<?> scaleRes_font = new ScalableFontResource(resId, resp_font);
        scaleCompServ.addScalableResource(scaleRes_font);
    }

    protected void preloadFonts()
    {
    }

    protected void preloadCards()
    {
        IIdService idServ = Services.get(IIdService.class);

        // card back
        preloadImage(idServ.createCardBackScalableResourceId(), getCardBackFilePath());

        // card fronts
        for (Suit suit : Suit.values())
        {
            for (Value value : Value.values()) // have fun reading the code lol
            {
                UUID resId = idServ.createCardFrontScalableResourceId(suit, value);
                String filepath = getCardFrontFilePath(suit, value);
                preloadImage(resId, filepath);
            }
        }
    }

    private String getCardFrontFilePath(Suit suit, Value value)
    {
        String suit_str = suit.name().substring(0, 1);
        String value_str = value.getTextual();

        return String.format(FILEPATH_CARD_FRONTSIDE, suit_str, value_str);
    }

    private String getCardBackFilePath()
    {
        return FILEPATH_CARD_BACKSIDE;
    }

    @Override
    public GamePanel createGamePanel()
    {
        if (gamePanel == null)
        {
            gamePanel = new GamePanel();
        }
        return gamePanel;
    }

    @Override
    public void fillGamePanel()
    {
        updatePositionManager();

        resizeListener = new GamePanelResizeListener();
        dragListener = new GamePanelMouseListener();
        stateListener = Services.get(IStateListener.class);

        gamePanel.addComponentListener(resizeListener);
        gamePanel.addMouseMotionListener(dragListener);
        gamePanel.addMouseListener(dragListener);
        Services.get(IContextService.class).getThreadContext().addStateListener(stateListener);

        animator.start();

        rescaleAsync();
        

        addScalableComponents();
        positionComponents();
    }

    protected void addScalableComponents()
    {
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState()
                .getCardGame();

        // card components
        List<ReadOnlyCard> cards = cardGame.getCards();
        for (int i = 0; i < cards.size(); i++)
        {
            ReadOnlyCard card = cards.get(i);
            IScalableComponent scaleComp = scaleCompServ.getOrCreateScalableComponent(card);
            add(scaleComp);
            updateComponent(scaleComp);
        }
    }

    @Override
    public void destroyGamePanel()
    {
        animator.stop();

        if (gamePanel != null)
        {
            gamePanel.removeComponentListener(resizeListener);
            gamePanel.removeMouseMotionListener(dragListener);
            gamePanel.removeMouseListener(dragListener);
        }

        Services.get(IContextService.class).getThreadContext().removeStateListener(stateListener);
        Services.get(IScalingService.class).clearManagedObjects();
        resizeListener = null;
        dragListener = null;
        gamePanel.removeAll();
        gamePanel = null;
        stateListener = null;
    }
    
    private void updatePositionManager()
    {
        // update the position service by supplying it with the latest game
        // panel dimensions
        int maxWidth = gamePanel.getWidth();
        int maxHeight = gamePanel.getHeight();
        IPositionService posMan = Services.get(IPositionService.class);
        posMan.setMaxSize(maxWidth, maxHeight);
    }

    @Override
    public void relayout()
    {
        updatePositionManager();
        positionComponents();
    }
    
    private void positionComponents()
    {
        IScalingService scaleServ = Services.get(IScalingService.class);
        for (IScalableComponent scaleComp : scaleServ.getComponents())
        {
            position(scaleComp);
        }
    }

    @Override
    public void setDrawDebug(boolean on)
    {
        drawDebug = on;

        if (gamePanel != null)
        {
            gamePanel.repaint();
        }
    }

    @Override
    public boolean getDrawDebug()
    {
        return drawDebug;
    }

    @Override
    public final void rescaleAsync()
    {
        IScalingService scaleServ = Services.get(IScalingService.class);
        List<RescaleRequest> requests = createRescaleRequests();
        scaleServ.rescaleAsync(requests, () -> gamePanel.repaint());
    }

    protected List<RescaleRequest> createRescaleRequests()
    {
        IIdService idServ = Services.get(IIdService.class);
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState()
                .getCardGame();

        List<RescaleRequest> requests = new ArrayList<RescaleRequest>();

        // cards - fronts
        for (ReadOnlyCard card : cardGame.getCards())
        {
            UUID resId = idServ.createCardFrontScalableResourceId(card.getSuit(), card.getValue());
            addRescaleRequest(requests, ComponentTypes.CARD, ResourceUsage.IMG_FRONT, resId);
        }
        // cards - back
        {
            UUID resId = idServ.createCardBackScalableResourceId();
            addRescaleRequest(requests, ComponentTypes.CARD, ResourceUsage.IMG_BACK, resId);
        }
        // card stacks
        for (ReadOnlyCardStack cs : cardGame.getCardStacks())
        {
            String csType = cs.getCardStackType();
            ComponentType cd_cardstack = ComponentTypes.CARDSTACK.derive(csType);           
            UUID resId = idServ.createCardStackScalableResourceId(csType);
            addRescaleRequest(requests, cd_cardstack, ResourceUsage.IMG_FRONT, resId);
        }

        return requests;
    }

    protected final void addRescaleRequest(List<RescaleRequest> requests, ComponentType compType,
            String resourceUsage, UUID resId)
    {
        IPositionService posServ = Services.get(IPositionService.class);
        IScalingService scaleServ = Services.get(IScalingService.class);

        // get dimension
        Dimension dim = posServ.getResourceDimension(compType, resourceUsage);

        // get the resource to rescale
        IScalableResource<?> res = scaleServ.getScalableResource(resId);

        // add rescale request for resource / dimension combination
        RescaleRequest rescReq = new RescaleRequest(res, dim);
        requests.add(rescReq);
    }

    @Override
    public void startAnimation(IScalableComponent scaleComp)
    {
        animator.animate(scaleComp);
    }

    @Override
    public void stopAnimation(IScalableComponent scaleComp)
    {
        animator.stopAnimate(scaleComp);
    }

    @Override
    public int getLayer(IScalableComponent scalableComponent)
    {
        return gamePanel.getLayer((Component) scalableComponent.getComponent());
    }

    public void setLayer(IScalableComponent component, int layerIndex)
    {
        gamePanel.setLayer(component.getComponent(), layerIndex);
    }

    private void position(IScalableComponent comp)
    {
        IPositionService posServ = Services.get(IPositionService.class);
        LayeredArea layArea = posServ.getStartLayeredArea(comp);
        comp.setCoords(layArea.coords);
        comp.setMirror(layArea.mirror);
        setLayer(comp, layArea.layer);
    }

    @Override
    public void add(IScalableComponent comp)
    {
        gamePanel.add(comp.getComponent());
        position(comp);
    }

    @Override
    public void remove(IScalableComponent comp)
    {
        gamePanel.remove(comp.getComponent());
        gamePanel.revalidate();
        gamePanel.repaint();
    }

    /**
     * Gets the next topmost, unoccupied animation layer.
     * 
     * @return
     */
    protected int getNextAnimationLayer()
    {
        IPositionService posServ = Services.get(IPositionService.class);

        Optional<Integer> maxLayerInUse = animator.getScalableComponents().stream().map(sc -> getLayer(sc))
                .max(Integer::compare);
        int layer = posServ.getAnimationLayer();
        if (maxLayerInUse.isPresent() && maxLayerInUse.get() > layer)
        {
            layer = maxLayerInUse.get() + 1;
        }

        return layer;
    }

    @Override
    public void updateComponent(IScalableComponent comp)
    {
        ComponentType compType = comp.getComponentType();
        if (compType.hasTypeName(ComponentTypes.CARD))
        {
            ScalableImageComponent cardComp = (ScalableImageComponent) comp;
            ReadOnlyCard card = (ReadOnlyCard) cardComp.getPayload();

            IIdService idServ = Services.get(IIdService.class);
            UUID resId = card.isVisible() ? idServ.createCardFrontScalableResourceId(card.getSuit(), card.getValue())
                    : idServ.createCardBackScalableResourceId();
            cardComp.setScalableImageResource(resId);
            cardComp.repaint();

            return;
        }
        
        if (compType.hasTypeName(ComponentTypes.CARDSTACK))
        {
            ScalableImageComponent cardStackComp = (ScalableImageComponent) comp;
            ReadOnlyCardStack cardStack = (ReadOnlyCardStack) cardStackComp.getPayload();
            
            IIdService idServ = Services.get(IIdService.class);
            UUID resId = idServ.createCardStackScalableResourceId(cardStack.getCardStackType());
            cardStackComp.setScalableImageResource(resId);
            return;
        }
        
        throw new UnsupportedOperationException("updateComponent not supported for ComponentType " + compType);
    }
}
