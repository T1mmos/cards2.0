package gent.timdemey.cards.services.gamepanel;

import java.awt.Color;
import java.awt.Component;
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
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.scaleman.IScalableComponent;
import gent.timdemey.cards.services.scaleman.IScalableResource;
import gent.timdemey.cards.services.scaleman.comps.CardScoreScalableTextComponent;
import gent.timdemey.cards.services.scaleman.img.ScalableImageResource;
import gent.timdemey.cards.services.scaleman.text.ScalableFontResource;
import gent.timdemey.cards.services.scaleman.text.ScalableTextComponent;

public class GamePanelService implements IGamePanelService
{
    private static final int ANIMATION_TIME_CARD = 80;
    private static final int ANIMATION_TIME_SCORE = 1200;

    private static final String FILEPATH_CARD_FRONTSIDE = "cards/edge_thick/%s_%s.png";
    private static final String FILEPATH_CARD_BACKSIDE = "backside_yellow.png";
    private static final String FILEPATH_FONT_SCORE = "SMB2.ttf";
    
    private final GamePanelAnimator animator;

    private GamePanelResizeListener resizeListener;
    private GamePanelMouseListener dragListener;
    private IStateListener gameEventListener;
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
        IIdService idServ = Services.get(IIdService.class);
        
        // score font        
        preloadFont(idServ.createFontResourceId(FILEPATH_FONT_SCORE), FILEPATH_FONT_SCORE);
    }    
    
    protected void preloadCards()
    {
        IIdService idServ = Services.get(IIdService.class);
        
        // card back
        preloadImage(idServ.createCardBackResourceId(), getCardBackFilePath());

        // card fronts
        for (Suit suit : Suit.values())
        {
            for (Value value : Value.values()) // have fun reading the code lol
            {
                UUID resId = idServ.createCardFrontResourceId(suit, value);
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
    
    private String getScoreFontFilePath ()
    {
        return FILEPATH_FONT_SCORE;
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
        addScalableComponents();

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

    protected void addScalableComponents()
    {
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();

        // card components
        List<ReadOnlyCard> cards = cardGame.getCards();
        for (int i = 0; i < cards.size(); i++)
        {
            ReadOnlyCard card = cards.get(i);
            IScalableComponent<?> scaleComp = scaleCompServ.getOrCreateScalableComponent(card);
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
        Services.get(IScalingService.class).clearManagedObjects();
        resizeListener = null;
        dragListener = null;
        gamePanel.removeAll();
        gamePanel = null;
        gameEventListener = null;
    }

    @Override
    public void relayout()
    {
        // update the position service by supplying it with the latest game
        // panel dimensions
        int maxWidth = gamePanel.getWidth();
        int maxHeight = gamePanel.getHeight();
        IPositionService posMan = Services.get(IPositionService.class);
        posMan.setMaxSize(maxWidth, maxHeight);

        // now update the position of all components
        IScalingService scaleCompService = Services.get(IScalingService.class);
        scaleCompService.setAllBounds();
        
        for (IScalableComponent<?> scaleComp : scaleCompService.getComponents())
        {
            LayeredArea layArea = posMan.getLayeredArea(scaleComp);
            
            setLayer(scaleComp, layArea.layer);
            scaleComp.setMirror(layArea.mirror);
        }

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
        IScalingService scaleCompServ = Services.get(IScalingService.class);
        scaleCompServ.rescaleAllResources(() -> gamePanel.repaint());
    }

    @Override
    public void animateCard(ReadOnlyCard card)
    {
        IIdService uuidServ = Services.get(IIdService.class);
        IScalingService scaleCompServ = Services.get(IScalingService.class);

        UUID compId = uuidServ.createCardComponentId(card);
        IScalableComponent<?> scaleComp = scaleCompServ.getScalableComponent(compId);

        LayeredArea layArea = Services.get(IPositionService.class).getLayeredArea(scaleComp);

        IPositionService posServ = Services.get(IPositionService.class);
       
        int layer = layArea.layer;
        layer += posServ.getDragLayer();
        
        setLayer(scaleComp, layer);

        int layerEnd = layArea.layer;
        MovingAnimation anim_pos = new MovingAnimation(scaleComp.getBounds().getLocation(), layArea.getLocation2D());
        AnimationEnd animEnd = new AnimationEnd(false, layerEnd);
        animator.animate(scaleComp, animEnd, ANIMATION_TIME_CARD, anim_pos);
    }

    @Override
    public void animateCardScore(ReadOnlyCard card, int oldScore, int newScore)
    {
        IScalingService scaleServ = Services.get(IScalingService.class);
        IIdService idServ = Services.get(IIdService.class);
        
        UUID resId = idServ.createFontResourceId(FILEPATH_FONT_SCORE);
        ScalableFontResource scaleFontRes = (ScalableFontResource) scaleServ.getScalableResource(resId);
        
        int incr = newScore - oldScore;
        ScalableTextComponent scaleTextComp = new CardScoreScalableTextComponent(UUID.randomUUID(), "+" + incr, scaleFontRes, card);

        add(scaleTextComp);
        IPositionService posServ = Services.get(IPositionService.class);
       
        LayeredArea layArea = posServ.getLayeredArea(scaleTextComp);
        scaleTextComp.setBounds(layArea.getBounds2D());
        setLayer(scaleTextComp, posServ.getAnimationLayer());
        scaleTextComp.update();
        
        IAnimation anim_color = new ForegroundColorAnimation(new Color(50, 100, 50, 255), new Color(255, 165, 0, 0));
        IAnimation anim_pos = new MovingAnimation(layArea.x, layArea.y, layArea.x, layArea.y - layArea.height);

        animator.animate(scaleTextComp, new AnimationEnd(true, -1), ANIMATION_TIME_SCORE, anim_color, anim_pos);
    }

    @Override
    public int getLayer(IScalableComponent<?> scalableComponent)
    {
        return gamePanel.getLayer((Component) scalableComponent.getComponent());
    }

    public void setLayer(IScalableComponent<?> component, int layerIndex)
    {
        gamePanel.setLayer(component.getComponent(), layerIndex);
    }

    @Override
    public void add(IScalableComponent<?> comp)
    {
        gamePanel.add(comp.getComponent());
    }

    @Override
    public void remove(IScalableComponent<?> comp)
    {
        gamePanel.remove(comp.getComponent());
    }

}
