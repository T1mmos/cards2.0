package gent.timdemey.cards.services.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.util.EnumSet;
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
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.panels.animations.PanelAnimator;
import gent.timdemey.cards.services.scaling.IScalableComponent;
import gent.timdemey.cards.services.scaling.IScalableResource;
import gent.timdemey.cards.services.scaling.img.ScalableImageComponent;

public class GamePanelManager extends DataPanelManagerBase<Void, Void>
{
    private GamePanel gamePanel;
    private PanelAnimator animator;
    private GamePanelMouseListener dragListener;

    @Override
    public EnumSet<PanelButtonType> getButtonTypes()
    {
        return null;
    }

    @Override
    public void preload()
    {
        
    }
        
    @Override
    public void onShown()
    {
        gamePanel.addMouseMotionListener(dragListener);
        gamePanel.addMouseListener(dragListener);
        
        gamePanel.repaint();
    }

    @Override
    public Void onClose(PanelButtonType dbType)
    {
        gamePanel.removeMouseMotionListener(dragListener);
        gamePanel.removeMouseListener(dragListener);
        
        return null;
    }

    @Override
    public boolean isButtonEnabled(PanelButtonType dbType)
    {
        return false;
    }

    @Override
    public boolean isCreated()
    {
        return gamePanel != null;
    }
    
    @Override
    public JComponent create()
    {
        gamePanel = new GamePanel();
        dragListener = new GamePanelMouseListener(); 
        animator = new PanelAnimator();           

        IStateListener stateListener = Services.get(IStateListener.class);
        Services.get(IContextService.class).getThreadContext().addStateListener(stateListener);
        animator.start();
        
        return gamePanel;
    }
    
    @Override
    public JComponent get()
    {
        return gamePanel;
    }
    
    @Override
    public void createScalableComponents()
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
    public void destroy()
    {
        animator.stop();

        IStateListener stateListener = Services.get(IStateListener.class);
        Services.get(IContextService.class).getThreadContext().removeStateListener(stateListener);
        
        gamePanel.removeAll();
        Services.get(IScalingService.class).clearComponentCache();
        
        dragListener = null;
        gamePanel = null;
    }
    
    private void add(IScalableComponent comp)
    {
        gamePanel.add(comp.getComponent());
        position(comp);
    }

    private void remove(IScalableComponent comp)
    {
        gamePanel.remove(comp.getComponent());
        gamePanel.revalidate();
        gamePanel.repaint();
    }
    
    private void position(IScalableComponent comp)
    {
        IPositionService posServ = Services.get(IPositionService.class);
        LayeredArea layArea = posServ.getStartLayeredArea(comp);
        comp.setCoords(layArea.coords);
        comp.setMirror(layArea.mirror);
        setLayer(comp, layArea.layer);
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


    public void startAnimation(IScalableComponent scaleComp)
    {
        int layer = getNextAnimationLayer();
        setLayer(scaleComp, layer);
        animator.animate(scaleComp);
    }

    public void stopAnimation(IScalableComponent scaleComp)
    {
        animator.stopAnimate(scaleComp);
    }

    
    public int getLayer(IScalableComponent scalableComponent)
    {
        return gamePanel.getLayer((Component) scalableComponent.getComponent());
    }

    public void setLayer(IScalableComponent component, int layerIndex)
    {
        gamePanel.setLayer(component.getComponent(), layerIndex);
    }
    
    protected final void addRescaleRequest(List<? super RescaleRequest> requests, ComponentType compType, UUID resId)
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
    public void positionScalableComponents()
    {
        IScalingService scaleServ = Services.get(IScalingService.class);
        for (IScalableComponent scaleComp : scaleServ.getComponents())
        {
            position(scaleComp);
        }
    }

    @Override
    public void repaintScalableComponents()
    {
        gamePanel.repaint();
    }
}
