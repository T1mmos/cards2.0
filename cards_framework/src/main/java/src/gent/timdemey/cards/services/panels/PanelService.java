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
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import gent.timdemey.cards.ICardPlugin;
import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.contract.preload.PreloadOrder;
import gent.timdemey.cards.services.contract.preload.PreloadOrderType;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.contract.res.ImageResource;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.panels.animations.PanelAnimator;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.IScalableResource;
import gent.timdemey.cards.services.scaling.img.ScalableImageComponent;
import gent.timdemey.cards.services.scaling.img.ScalableImageResource;
import gent.timdemey.cards.services.scaling.text.ScalableFontResource;
import gent.timdemey.cards.ui.actions.ActionDescriptor;
import gent.timdemey.cards.ui.actions.ActionDescriptors;
import gent.timdemey.cards.ui.actions.IActionService;
import net.miginfocom.swing.MigLayout;

public class PanelService implements IPanelService
{

    private final PanelAnimator animator;

    private GamePanelResizeListener resizeListener;
    private GamePanelMouseListener dragListener;

    // different panels
    private GamePanel gamePanel;
    private LoadPanel loadPanel;
    private MenuPanel menuPanel;

    private boolean loaded = false;
    
    public PanelService()
    {
        this.animator = new PanelAnimator();
    }

    @Override
    @PreloadOrder(order = PreloadOrderType.DEPENDENT)
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
        IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);

        // card back
        preloadImage(idServ.createCardBackScalableResourceId(), resLocServ.getCardBackFilePath());

        // card fronts
        for (Suit suit : Suit.values())
        {
            for (Value value : Value.values()) // have fun reading the code lol
            {
                UUID resId = idServ.createCardFrontScalableResourceId(suit, value);
                preloadImage(resId, resLocServ.getCardFrontFilePath(suit, value));
            }
        }
    }

    @Override
    public List<PanelDescriptor> getPanelDescriptors()
    {
        return Arrays.asList(new PanelDescriptor[]
        { PanelDescriptors.GAME, PanelDescriptors.LOAD, PanelDescriptors.MENU });
    }
    
    @Override
    public JComponent getPanel(PanelDescriptor desc)
    {
        if(desc == PanelDescriptors.GAME)
        {
            if(gamePanel == null)
            {
                gamePanel = new GamePanel();
            }
            return gamePanel;
        }

        if (desc == PanelDescriptors.LOAD) 
        {
            if(loadPanel == null)
            {
                loadPanel = new LoadPanel();
                
                loadPanel.setLayout(new MigLayout("insets 0, align 50% 50%"));
                loadPanel.add(new JLabel("LOADING..."));
            }
            return loadPanel;
        }
        
        if(desc == PanelDescriptors.MENU)
        {
            if(menuPanel == null)
            {
                menuPanel = new MenuPanel();
                menuPanel.setLayout(new MigLayout("insets 0, align 50% 50%"));

                List<ActionDescriptor> actDescs = getMenuActionDescriptors();

                MenuButtonMouseListener listener = new MenuButtonMouseListener();
                IResourceLocationService resLocServ = Services.get(IResourceLocationService.class);
                IResourceService resServ = Services.get(IResourceService.class);
                
                Font font = resServ.getFont(resLocServ.getMenuFontFilePath()).raw.deriveFont(30f);

                IActionService actServ = Services.get(IActionService.class);
                for (ActionDescriptor actDesc : actDescs)
                {
                    JButton button = new JButton(actServ.getAction(actDesc));
                    button.setContentAreaFilled(false);
                    button.setFocusPainted(false);
                    button.setBorder(null);
                    button.setBorderPainted(false);
                    button.setOpaque(false);

                    button.addMouseListener(listener);
                    button.setFont(font);
                    button.setIcon(null); // no icon in menu list

                    menuPanel.add(button, "sg buts, wrap");
                }
            }
            return menuPanel;
        }

        throw new IllegalArgumentException(String.format("Unsupported PanelDescriptor id: %s", desc.id));
    }    

    @Override
    public void createPanel(PanelDescriptor desc)
    {
        if (desc == PanelDescriptors.GAME)
        {                        
            resizeListener = new GamePanelResizeListener();
            dragListener = new GamePanelMouseListener();            

            IStateListener stateListener = Services.get(IStateListener.class);
            Services.get(IContextService.class).getThreadContext().addStateListener(stateListener);

            animator.start();

            updatePositionManager();
            rescaleResourcesAsync();
        }
    }
    
    @Override
    public void destroyPanel(PanelDescriptor desc)
    {
        if(desc == PanelDescriptors.GAME)
        {
            if(gamePanel != null)
            {
                animator.stop();

                IStateListener stateListener = Services.get(IStateListener.class);
                Services.get(IContextService.class).getThreadContext().removeStateListener(stateListener);
                
                destroyScalableComponents();
                
                resizeListener = null;
                dragListener = null;
                loaded = false;
            }
        }
        else if (desc == PanelDescriptors.LOAD) 
        {
            if (loadPanel != null)
            {
                loadPanel = null;
            }
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
            
            gamePanel.repaint();
        }
        else if (desc == PanelDescriptors.LOAD) 
        {
            loadPanel.setVisible(true);
        }
    }

    @Override
    public void onPanelHidden(PanelDescriptor desc)
    {
        if (desc == PanelDescriptors.GAME)
        {
            gamePanel.removeComponentListener(resizeListener);
            gamePanel.removeMouseMotionListener(dragListener);
            gamePanel.removeMouseListener(dragListener);
        }
        else if (desc == PanelDescriptors.LOAD)
        {
            loadPanel.setVisible(false);
        }
    }

    protected List<ActionDescriptor> getMenuActionDescriptors()
    {
        ICardPlugin cardPlugin = Services.get(ICardPlugin.class);
        if(cardPlugin.getPlayerCount() > 1)
        {
            return Arrays.asList(ActionDescriptors.ad_create_mp, ActionDescriptors.ad_join, ActionDescriptors.ad_quit);
        }
        else
        {
            return Arrays.asList(ActionDescriptors.ad_start, ActionDescriptors.ad_quit);
        }

    }

    protected void createScalableComponents()
    {
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();

        // card components
        List<ReadOnlyCard> cards = cardGame.getCards();
        for (int i = 0; i < cards.size(); i++)
        {
            ReadOnlyCard card = cards.get(i);
            IScalableComponent scaleComp = scaleCompServ.createScalableComponent(card);
            add(scaleComp);
            updateComponent(scaleComp);
        }
    }
    
    protected void destroyScalableComponents()
    {
        gamePanel.removeAll();
        Services.get(IScalingService.class).clearComponentCache();
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
        positionScalableComponents();
    }

    private void positionScalableComponents()
    {
        IScalingService scaleServ = Services.get(IScalingService.class);
        for (IScalableComponent scaleComp : scaleServ.getComponents())
        {
            position(scaleComp);
        }
    }

    @Override
    public final void rescaleResourcesAsync()
    {
        IScalingService scaleServ = Services.get(IScalingService.class);
        List<RescaleRequest> requests = createRescaleRequests();
        scaleServ.rescaleAsync(requests, () -> 
        {
            SwingUtilities.invokeLater(() -> onResourcesRescaled());           
        });
    }
    
    private void onResourcesRescaled()
    {
        if (!loaded)
        {
            loaded = true;
            createScalableComponents();
            positionScalableComponents();
            IFrameService frameServ = Services.get(IFrameService.class);
            frameServ.hidePanel(PanelDescriptors.LOAD);
        }
        
        gamePanel.repaint();
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
        int layer = getNextAnimationLayer();
        setLayer(scaleComp, layer);
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
