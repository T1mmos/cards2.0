package gent.timdemey.cards.services.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JComponent;

import gent.timdemey.cards.ICardPlugin;
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
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.panels.animations.GamePanelAnimator;
import gent.timdemey.cards.services.resources.ResourceDefines;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.IScalableResource;
import gent.timdemey.cards.services.scaling.img.ScalableImageComponent;
import gent.timdemey.cards.services.scaling.img.ScalableImageResource;
import gent.timdemey.cards.services.scaling.text.ScalableFontResource;
import gent.timdemey.cards.ui.actions.ActionDef;
import gent.timdemey.cards.ui.actions.Actions;
import gent.timdemey.cards.ui.actions.IActionFactory;
import net.miginfocom.swing.MigLayout;

public class PanelService implements IPanelService
{
    private static final String FILEPATH_CARD_FRONTSIDE = "cards/edge_thick/%s_%s.png";
    private static final String FILEPATH_CARD_BACKSIDE = "cards/edge_thick/backside_yellow.png";

    private final GamePanelAnimator animator;

    private GamePanelResizeListener resizeListener;
    private GamePanelMouseListener dragListener;
    // private IStateListener stateListener;

    // different panels
    private GamePanel gamePanel;
    private MenuPanel menuPanel;

    public PanelService()
    {
        this.animator = new GamePanelAnimator();
    }

    @Override
    public void preload()
    {
        preloadFonts();
        preloadCards();
    }

    protected final void preloadImage(UUID resId, String path)
    {
        IResourceService resServ = Services.get(IResourceService.class);
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        ImageResource imgRes = resServ.getImage(path);
        IScalableResource<?> scaleRes_back = new ScalableImageResource(resId, imgRes);
        scaleCompServ.addScalableResource(scaleRes_back);
    }

    protected final void preloadFont(UUID resId, String path)
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
    public List<PanelDescriptor> getPanelDescriptors()
    {
        return Arrays.asList(new PanelDescriptor[]
        { PanelDescriptors.GAME, PanelDescriptors.MENU });
    }

    @Override
    public JComponent getPanel(PanelDescriptor desc)
    {
        if(desc == PanelDescriptors.GAME)
        {
            if(gamePanel == null)
            {
                gamePanel = new GamePanel();
                resizeListener = new GamePanelResizeListener();
                dragListener = new GamePanelMouseListener();
            }
            return gamePanel;
        }

        if(desc == PanelDescriptors.MENU)
        {
            if(menuPanel == null)
            {
                menuPanel = new MenuPanel();
                menuPanel.setLayout(new MigLayout("insets 0, align 50% 50%"));

                List<String> actionNames = getMenuActionDefs();

                MenuButtonMouseListener listener = new MenuButtonMouseListener();
                IResourceService resServ = Services.get(IResourceService.class);
                Font font = resServ.getFont(ResourceDefines.FILEPATH_FONT_MENU).raw.deriveFont(30f);

                IActionFactory actFact = Services.get(IActionFactory.class);
                for (String actionName : actionNames)
                {
                    ActionDef act_createmp = actFact.getActionDef(actionName);
                    JButton button = new JButton(act_createmp.action);
                    button.setContentAreaFilled(false);
                    button.setBorder(null);
                    button.setBorderPainted(false);
                    button.setOpaque(false);

                    button.addMouseListener(listener);
                    button.setFont(font);

                    menuPanel.add(button, "sg buts, wrap");
                }
            }
            return menuPanel;
        }

        throw new IllegalArgumentException(String.format("Unsupported PanelDescriptor id: %s", desc.id));
    }

    protected List<String> getMenuActionDefs()
    {
        ICardPlugin cardPlugin = Services.get(ICardPlugin.class);
        if(cardPlugin.getPlayerCount() > 1)
        {
            return Arrays.asList(Actions.ACTION_CREATE_MULTIPLAYER, Actions.ACTION_JOIN, Actions.ACTION_QUIT);
        }
        else
        {
            return Arrays.asList(Actions.ACTION_START, Actions.ACTION_QUIT);
        }

    }

    @Override
    public void onPanelShown(PanelDescriptor desc)
    {
        if(desc == PanelDescriptors.GAME)
        {
            gamePanel.addComponentListener(resizeListener);
            gamePanel.addMouseMotionListener(dragListener);
            gamePanel.addMouseListener(dragListener);

            updatePositionManager();

            IStateListener stateListener = Services.get(IStateListener.class);
            Services.get(IContextService.class).getThreadContext().addStateListener(stateListener);

            animator.start();

            rescaleAsync();

            addScalableComponents();
            positionComponents();
        }
        else if(desc == PanelDescriptors.MENU)
        {

        }
    }

    @Override
    public void onPanelHidden(PanelDescriptor desc)
    {

    }

    protected void addScalableComponents()
    {
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();

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
    public void destroyPanel(PanelDescriptor desc)
    {
        animator.stop();

        if(desc == PanelDescriptors.GAME)
        {
            if(gamePanel != null)
            {
                gamePanel.removeComponentListener(resizeListener);
                gamePanel.removeMouseMotionListener(dragListener);
                gamePanel.removeMouseListener(dragListener);
                gamePanel.removeAll();

                IStateListener stateListener = Services.get(IStateListener.class);
                Services.get(IContextService.class).getThreadContext().removeStateListener(stateListener);
                Services.get(IScalingService.class).clearManagedObjects();
                resizeListener = null;
                dragListener = null;

                gamePanel = null;
            }
        }
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
    public final void rescaleAsync()
    {
        IScalingService scaleServ = Services.get(IScalingService.class);
        List<RescaleRequest> requests = createRescaleRequests();
        scaleServ.rescaleAsync(requests, () -> gamePanel.repaint());
    }

    protected List<RescaleRequest> createRescaleRequests()
    {
        IIdService idServ = Services.get(IIdService.class);
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();

        List<RescaleRequest> requests = new ArrayList<RescaleRequest>();

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

        return requests;
    }

    protected final void addRescaleRequest(List<RescaleRequest> requests, ComponentType compType, UUID resId)
    {
        IPositionService posServ = Services.get(IPositionService.class);
        IScalingService scaleServ = Services.get(IScalingService.class);

        // get dimension
        Dimension dim = posServ.getResourceDimension(compType);

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

        Optional<Integer> maxLayerInUse = animator.getScalableComponents().stream().map(sc -> getLayer(sc)).max(Integer::compare);
        int layer = posServ.getAnimationLayer();
        if(maxLayerInUse.isPresent() && maxLayerInUse.get() > layer)
        {
            layer = maxLayerInUse.get() + 1;
        }

        return layer;
    }

    @Override
    public void updateComponent(IScalableComponent comp)
    {
        ComponentType compType = comp.getComponentType();
        if(compType.hasTypeName(ComponentTypes.CARD))
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

        if(compType.hasTypeName(ComponentTypes.CARDSTACK))
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
