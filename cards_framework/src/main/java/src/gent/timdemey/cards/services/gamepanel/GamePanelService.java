package gent.timdemey.cards.services.gamepanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.FontResource;
import gent.timdemey.cards.services.contract.ImageResource;
import gent.timdemey.cards.services.gamepanel.animations.AnimationEnd;
import gent.timdemey.cards.services.gamepanel.animations.ColorAnimation;
import gent.timdemey.cards.services.gamepanel.animations.GamePanelAnimator;
import gent.timdemey.cards.services.gamepanel.animations.IAnimation;
import gent.timdemey.cards.services.gamepanel.animations.MovingAnimation;
import gent.timdemey.cards.services.interfaces.IScalableComponentService;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IGamePanelService;
import gent.timdemey.cards.services.interfaces.IPositionService;
import gent.timdemey.cards.services.interfaces.IResourceService;
import gent.timdemey.cards.services.interfaces.IUUIDService;
import gent.timdemey.cards.services.scaleman.IScalableComponent;
import gent.timdemey.cards.services.scaleman.IScalableResource;
import gent.timdemey.cards.services.scaleman.comps.CardScalableImageComponent;
import gent.timdemey.cards.services.scaleman.img.ScalableImageComponent;
import gent.timdemey.cards.services.scaleman.img.ScalableImageResource;

public class GamePanelService implements IGamePanelService
{
    private static final int ANIMATION_TIME_CARD = 80;
    private static final int ANIMATION_TIME_SCORE = 1200;

    private static final String FILEPATH_FRONTSIDE = "cards/edge_thick/%s_%s.png";
    private static final String FILEPATH_BACKSIDE = "backside_yellow.png";

    static final int LAYER_CARDSTACKS = 0;
    static final int LAYER_CARDS = 200;
    static final int LAYER_DRAG = 10000;
    static final int LAYER_ANIMATIONS = 20000;

    private final GamePanelAnimator animator;

    private GamePanelResizeListener resizeListener;
    private GamePanelMouseListener dragListener;
    private IStateListener gameEventListener;
    private boolean drawDebug = false;
    protected GamePanel gamePanel;
    private Font scoreFont;

    private final Map<JComponent, IScalableComponent> reverseCompMap;

    public GamePanelService()
    {
        this.animator = new GamePanelAnimator();
        this.reverseCompMap = new HashMap<>();
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
        UUID resBackId = uuidServ.getCardBackResourceId();
        IScalableResource scaleRes_back = new ScalableImageResource(resBackId, imgRes_back);
        scaleCompServ.addScalableResource(scaleRes_back);

        // card fronts
        for (Suit suit : Suit.values())
        {
            for (Value value : Value.values()) // have fun reading the code lol
            {
                UUID resId = uuidServ.getCardFrontResourceId(suit, value);
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
    public void createGamePanel(int w, int h)
    {
        if (gamePanel == null)
        {
            gamePanel = new GamePanel();
            gamePanel.setBounds(0, 0, w, h);

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

    private void updatePositionManager()
    {
        IPositionService posMan = Services.get(IPositionService.class);
        int maxWidth = gamePanel.getWidth();
        int maxHeight = gamePanel.getHeight();
        posMan.setMaxSize(maxWidth, maxHeight);
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
        gamePanel = null;
        gameEventListener = null;
    }

    @Override
    public void relayout()
    {
        updatePositionManager();

        for (IScalableComponent comp : getScalableComponents())
        {
            updateOrAnimatePosition(comp, true);
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
        IScalableComponentService scaleCompServ = Services.get(IScalableComponentService.class);
        
        scaleCompServ.setAllBounds();        
        scaleCompServ.rescaleResources(() -> scaleCompServ.updateComponents());
    }

    @Override
    public void animatePosition(ReadOnlyCard card)
    {
        updateOrAnimatePosition(card, false);
    }

    private void updateOrAnimatePosition(IScalableComponent scaleComp, boolean update)
    {
        String id = scaleComp.getId();

        IScalableComponentService scaleCompServ = Services.get(IScalableComponentService.class);
        ReadOnlyCardStack cardStack = card.getCardStack();
        int layerIndex = LAYER_CARDS + 20 * cardStack.getTypeNumber() + card.getCardIndex();
        Rectangle rect_dst = Services.get(IPositionManager.class).getBounds(modelId);

        if (update)
        {
            setLayer(scaleComp, layerIndex);
            scaleComp.setBounds(rect_dst.x, rect_dst.y, rect_dst.width, rect_dst.height);
        }
        else
        {
            MovingAnimation anim_pos = new MovingAnimation(scaleComp.getBounds().getLocation(), rect_dst.getLocation());
            animator.animate(scaleComp, new AnimationEnd(false, layerIndex), ANIMATION_TIME_CARD, anim_pos);
        }
    }

    @Override
    public void updatePosition(ReadOnlyCardStack cardStack)
    {
        IScalableComponent jcardstack = Services.get(IScalableImageManager.class).getScalableImage(cardStack.getId());
        Rectangle rect_dst = Services.get(IPositionManager.class).getBounds(cardStack.getId());

        int layerIndex = LAYER_CARDSTACKS;
        setLayer(jcardstack, layerIndex);

        jcardstack.setBounds(rect_dst.x, rect_dst.y, rect_dst.width, rect_dst.height);
    }

    public void animatePlayerScore(UUID playerId, int oldScore, int newScore)
    {
        /* animate the player score when it is implemented */
    }

    @Override
    public void animateCardScore(UUID cardId, int oldScore, int newScore)
    {
        int incr = newScore - oldScore;
        ScalableText scaleText = new ScalableText("+" + incr, scoreFont);

        add(scaleText);
        setLayer(scaleText, LAYER_ANIMATIONS);

        // determine final card position (the card itself may still be animated into its
        // final position)
        // to calculate the animation's start position
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        Rectangle rect_dst = Services.get(IPositionManager.class).getBounds(cardId);
        int posx = rect_dst.x + (rect_dst.width - scaleText.getWidth()) / 2;
        int posy = rect_dst.y;

        IAnimation anim_color = new ColorAnimation(new Color(50, 100, 50, 255), new Color(255, 165, 0, 0));
        IAnimation anim_pos = new MovingAnimation(posx, posy, posx, posy - 100);

        animator.animate(scaleText, new AnimationEnd(true, -1), ANIMATION_TIME_SCORE, anim_color, anim_pos);
    }

    @Override
    public void animatePosition(ReadOnlyCardStack cardStack)
    {
        // TODO Auto-generated method stub

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
        JComponent jcomp = comp.getComponent();
        gamePanel.add(jcomp);
        reverseCompMap.put(jcomp, comp);
    }

    @Override
    public void remove(IScalableComponent comp)
    {
        JComponent jcomp = comp.getComponent();
        gamePanel.remove(jcomp);
        reverseCompMap.remove(jcomp);
    }

    @Override
    public IScalableComponent get(Object uiComp)
    {
        IScalableComponent scaleComp = reverseCompMap.get(uiComp);
        return scaleComp;
    }

    @Override
    public Collection<IScalableComponent> getScalableComponents()
    {
        return Collections.unmodifiableCollection(reverseCompMap.values());
    }
}
