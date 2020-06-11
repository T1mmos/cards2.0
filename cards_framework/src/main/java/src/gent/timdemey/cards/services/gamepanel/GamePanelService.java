package gent.timdemey.cards.services.gamepanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.services.contract.FontResource;
import gent.timdemey.cards.services.contract.ImageResource;
import gent.timdemey.cards.services.contract.LayeredArea;
import gent.timdemey.cards.services.gamepanel.animations.AnimationEnd;
import gent.timdemey.cards.services.gamepanel.animations.ForegroundColorAnimation;
import gent.timdemey.cards.services.gamepanel.animations.GamePanelAnimator;
import gent.timdemey.cards.services.gamepanel.animations.IAnimation;
import gent.timdemey.cards.services.gamepanel.animations.MovingAnimation;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.services.interfaces.IScalableComponentService;
import gent.timdemey.cards.services.interfaces.IUUIDService;
import gent.timdemey.cards.services.scaleman.IScalableComponent;
import gent.timdemey.cards.services.scaleman.IScalableResource;
import gent.timdemey.cards.services.scaleman.comps.CardScoreScalableTextComponent;
import gent.timdemey.cards.services.scaleman.img.ScalableImageResource;
import gent.timdemey.cards.services.scaleman.text.ScalableTextComponent;

public class GamePanelService implements IGamePanelService
{
    private static final int ANIMATION_TIME_CARD = 80;
    private static final int ANIMATION_TIME_SCORE = 1200;

    private static final String FILEPATH_FRONTSIDE = "cards/edge_thick/%s_%s.png";
    private static final String FILEPATH_BACKSIDE = "backside_yellow.png";

    private final GamePanelAnimator animator;

    private GamePanelResizeListener resizeListener;
    private GamePanelMouseListener dragListener;
    private IStateListener gameEventListener;
    private boolean drawDebug = false;
    protected GamePanel gamePanel;
    private Font scoreFont;

    public GamePanelService()
    {
        this.animator = new GamePanelAnimator();
    }

    @Override
    public void preload()
    {
        IScalableComponentService scaleCompServ = Services.get(IScalableComponentService.class);
        IUUIDService uuidServ = Services.get(IUUIDService.class);

        // score font
        IResourceService resServ = Services.get(IResourceService.class);
        FontResource resp_font = resServ.getFont("SMB2.ttf");
        scoreFont = resp_font.font.deriveFont(52f);

        // card back
        ImageResource imgRes_back = resServ.getImage(getCardBackFilePath());
        UUID resBackId = uuidServ.createCardBackResourceId();
        IScalableResource scaleRes_back = new ScalableImageResource(resBackId, imgRes_back);
        scaleCompServ.addScalableResource(scaleRes_back);

        // card fronts
        for (Suit suit : Suit.values())
        {
            for (Value value : Value.values()) // have fun reading the code lol
            {
                UUID resId = uuidServ.createCardFrontResourceId(suit, value);
                String filepath = getCardFrontFilePath(suit, value);

                ImageResource imgRes_front = resServ.getImage(filepath);
                IScalableResource scaleRes_front = new ScalableImageResource(resId, imgRes_front);
                scaleCompServ.addScalableResource(scaleRes_front);
            }
        }
    }

    private String getCardFrontFilePath(Suit suit, Value value)
    {
        String suit_str = suit.name().substring(0, 1);
        String value_str = value.getTextual();

        return String.format(FILEPATH_FRONTSIDE, suit_str, value_str);
    }

    private String getCardBackFilePath()
    {
        return FILEPATH_BACKSIDE;
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
        createScalableComponents();

        resizeListener = new GamePanelResizeListener();
        dragListener = new GamePanelMouseListener();
        gameEventListener = new GamePanelStateListener();

        gamePanel.addComponentListener(resizeListener);
        gamePanel.addMouseMotionListener(dragListener);
        gamePanel.addMouseListener(dragListener);
        Services.get(IContextService.class).getThreadContext().addStateListener(gameEventListener);

        relayout();
        animator.start();

        rescaleAsync();
    }

    protected void createScalableComponents()
    {
        IScalableComponentService scaleCompServ = Services.get(IScalableComponentService.class);

        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState()
                .getCardGame();
        
        // card components
        List<ReadOnlyCard> cards = cardGame.getCards();
        for (int i = 0; i < cards.size(); i++)
        {
            ReadOnlyCard card = cards.get(i);
            IScalableComponent scaleComp = scaleCompServ.getOrCreate(card);
            add(scaleComp);
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

        Services.get(IContextService.class).getThreadContext().removeStateListener(gameEventListener);
        Services.get(IScalableComponentService.class).clearManagedObjects();
        resizeListener = null;
        dragListener = null;
        gamePanel.removeAll();
        gamePanel = null;
        gameEventListener = null;
    }

    @Override
    public void relayout()
    {
        // update the position service by supplying it with the latest game panel dimensions
        int maxWidth = gamePanel.getWidth();
        int maxHeight = gamePanel.getHeight();
        IPositionService posMan = Services.get(IPositionService.class);
        posMan.setMaxSize(maxWidth, maxHeight);

        // now update the position of all components
        IScalableComponentService scaleCompService = Services.get(IScalableComponentService.class);
        scaleCompService.setAllBounds();

        gamePanel.repaint();
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

    public void rescaleAsync()
    {
        IScalableComponentService scaleCompServ = Services.get(IScalableComponentService.class);               
        scaleCompServ.rescaleAllResources(() -> gamePanel.repaint());
    }

    @Override
    public void animateCard(ReadOnlyCard card)
    {
        IUUIDService uuidServ = Services.get(IUUIDService.class);
        IScalableComponentService scaleCompServ = Services.get(IScalableComponentService.class);
        
        UUID compId = uuidServ.createCardComponentId(card);        
        IScalableComponent scaleComp = scaleCompServ.getScalableComponent(compId);
        updateOrAnimatePosition(scaleComp, true);
    }
    
    private void updateOrAnimatePosition(IScalableComponent scaleComp, boolean animate)
    {
        LayeredArea layArea = Services.get(IPositionService.class).getLayeredArea(scaleComp, animate);
        
        // set current layer
        int layerNow = layArea.getLayer(animate);
        setLayer(scaleComp, layerNow);
        
        if (animate)
        {
            int layerEnd = layArea.getLayer(false);
            MovingAnimation anim_pos = new MovingAnimation(scaleComp.getBounds().getLocation(), layArea.getLocation2D());
            AnimationEnd animEnd = new AnimationEnd(false, layerEnd);
            animator.animate(scaleComp, animEnd, ANIMATION_TIME_CARD, anim_pos);
        }
        else
        {
            scaleComp.setBounds(layArea.getBounds2D());
        }
    }

    @Override
    public void animateCardScore(ReadOnlyCard card, int oldScore, int newScore)
    {
        int incr = newScore - oldScore;
        ScalableTextComponent scaleTextComp = new CardScoreScalableTextComponent(UUID.randomUUID(), "+" + incr, scoreFont, card);

        add(scaleTextComp);
        IPositionService posServ = Services.get(IPositionService.class);
        LayeredArea layArea = posServ.getLayeredArea(scaleTextComp, true);
        setLayer(scaleTextComp, layArea.getLayer(true));
        setZOrder(scaleTextComp, layArea.getZOrder());

        // determine final card position (the card itself may still be animated into its
        // final position)
        // to calculate the animation's start position
      

        IAnimation anim_color = new ForegroundColorAnimation(new Color(50, 100, 50, 255), new Color(255, 165, 0, 0));
        IAnimation anim_pos = new MovingAnimation(layArea.x, layArea.y, layArea.x, layArea.y - layArea.height);

        animator.animate(scaleTextComp, new AnimationEnd(true, -1), ANIMATION_TIME_SCORE, anim_color, anim_pos);
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

    @Override
    public int getZOrder(IScalableComponent scalableComponent)
    {
        return gamePanel.getComponentZOrder(scalableComponent.getComponent());
    }

    @Override
    public void setZOrder(IScalableComponent component, int zorder)
    {
        gamePanel.setComponentZOrder(component.getComponent(), zorder);
    }
    
    @Override
    public void add(IScalableComponent comp)
    {
        gamePanel.add(comp.getComponent());
    }
    
    @Override
    public void remove(IScalableComponent comp)
    {
        gamePanel.remove(comp.getComponent());
    }
}
