package gent.timdemey.cards.services.gamepanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IFontService;
import gent.timdemey.cards.services.IGamePanelService;
import gent.timdemey.cards.services.IPositionManager;
import gent.timdemey.cards.services.IScalableImageManager;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.gamepanel.animations.AnimationEnd;
import gent.timdemey.cards.services.gamepanel.animations.ColorAnimation;
import gent.timdemey.cards.services.gamepanel.animations.GamePanelAnimator;
import gent.timdemey.cards.services.gamepanel.animations.IAnimation;
import gent.timdemey.cards.services.gamepanel.animations.MovingAnimation;
import gent.timdemey.cards.services.scaleman.ImageDefinition;
import gent.timdemey.cards.services.scaleman.JScalableImage;

public class GamePanelService implements IGamePanelService
{
    private static final int ANIMATION_TIME_CARD = 80;
    private static final int ANIMATION_TIME_SCORE = 1200;

    private static final String SCALEGROUP_CARDS = "SCALEGROUP_CARDS";
    private static final String FILENAME_BACKSIDE = "backside_yellow.png";

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

    public GamePanelService()
    {
        this.animator = new GamePanelAnimator();
    }

    @Override
    public void createGamePanel(int w, int h, final IGamePanelReceiver callback)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());

        if (gamePanel == null)
        {
            gamePanel = new GamePanel();
            gamePanel.setBounds(0, 0, w, h);

            addScalableImages();

            resizeListener = new GamePanelResizeListener();
            dragListener = new GamePanelMouseListener();
            gameEventListener = new GamePanelStateListener();

            gamePanel.addComponentListener(resizeListener);
            gamePanel.addMouseMotionListener(dragListener);
            gamePanel.addMouseListener(dragListener);
            Services.get(IContextService.class).getThreadContext().addStateListener(gameEventListener);
            
            relayout();
            animator.start();
            
            IFontService fontServ = Services.get(IFontService.class);
            Font f = fontServ.getFont("SMB2.ttf");
            scoreFont = f.deriveFont(52f);
            
            rescaleAsync(() ->
            {
                SwingUtilities.invokeLater(() -> callback.onPanelCreated(gamePanel));
            });
        }
    }
    
    private void updatePositionManager()
    {
        IPositionManager posMan = Services.get(IPositionManager.class);
        int maxWidth = gamePanel.getWidth();
        int maxHeight = gamePanel.getHeight();
        posMan.setMaxSize(maxWidth, maxHeight);
    }

    protected void addScalableImages()
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState()
                .getCardGame();

        List<ReadOnlyCard> cards = cardGame.getCards();
        for (int i = 0; i < cards.size(); i++)
        {
            ReadOnlyCard card = cards.get(i);
            JScalableImage jscalable = Services.get(IScalableImageManager.class).getJScalableImage(card.getId());

            String filename = card.isVisible() ? getFilename(card) : FILENAME_BACKSIDE;
            Services.get(IScalableImageManager.class).setImage(card.getId(), filename);

            gamePanel.add(jscalable);
        }
    }

    @Override
    public void destroyGamePanel()
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());

        animator.stop();
        
        if (gamePanel != null)
        {
            gamePanel.removeComponentListener(resizeListener);
            gamePanel.removeMouseMotionListener(dragListener);
            gamePanel.removeMouseListener(dragListener);
        }

        Services.get(IContextService.class).getThreadContext().removeStateListener(gameEventListener);
        Services.get(IScalableImageManager.class).clearManagedObjects();
        resizeListener = null;
        dragListener = null;
        gamePanel = null;
        gameEventListener = null;
    }

    @Override
    public void relayout()
    {
        Context context = Services.get(IContextService.class).getThreadContext();
        ReadOnlyCardGame cardGame = context.getReadOnlyState().getCardGame();
        
        updatePositionManager();

        for (ReadOnlyCardStack cardStack : cardGame.getCardStacks())
        {
            updateOrAnimatePosition(cardStack);
        }
        for (ReadOnlyCard card : cardGame.getCards())
        {
            updateOrAnimatePosition(card, true);
        }

        gamePanel.repaint();
    }

    @Override
    public void setDrawDebug(boolean on)
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
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
    public final void rescaleAsync(Runnable callback)
    {
        rescaleAsync();

        Services.get(IScalableImageManager.class).rescaleAsync(callback);
    }

    protected void rescaleAsync()
    {
        IPositionManager posMan = Services.get(IPositionManager.class);
        {
            Rectangle rect = posMan.getCardSize();
            if (rect.width > 0 && rect.height > 0)
            {
                Services.get(IScalableImageManager.class).setSize(SCALEGROUP_CARDS, rect.width, rect.height);
            }
        }

        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState()
                .getCardGame();

        for (String cardStackType : cardGame.getCardStackTypes())
        {
            String scalegroup_id = cardStackType;
            Rectangle rect = posMan.getCardStackSize(cardStackType);
            if (rect.width > 0 && rect.height > 0)
            {
                Services.get(IScalableImageManager.class).setSize(scalegroup_id, rect.width, rect.height);
            }
        }
    }

    @Override
    public List<ImageDefinition> getScalableImageDefinitions()
    {
        Context context = Services.get(IContextService.class).getThreadContext();
        ReadOnlyCardGame cardGame = context.getReadOnlyState().getCardGame();
        List<ImageDefinition> imgDefs = new ArrayList<>();

        // add card fronts
        List<ReadOnlyCard> cards = cardGame.getCards();
        for (ReadOnlyCard card : cards)
        {
            String filename = getFilename(card);

            ImageDefinition def = new ImageDefinition(filename, SCALEGROUP_CARDS);
            imgDefs.add(def);
        }
        // add backside
        imgDefs.add(new ImageDefinition(FILENAME_BACKSIDE, SCALEGROUP_CARDS));

        return imgDefs;
    }

    private String getFilename(ReadOnlyCard card)
    {
        String suit = card.getSuit().name().substring(0, 1);
        String value = card.getValue().getTextual();
        String full = String.format("cards/edge_thick/%s_%s.png", suit, value);

        return full;
    }

    @Override
    public void setVisible(ReadOnlyCard card, boolean visible)
    {
        String whatToShow = visible ? getFilename(card) : FILENAME_BACKSIDE;
        Services.get(IScalableImageManager.class).setImage(card.getId(), whatToShow);
    }


    @Override
    public void animatePosition(ReadOnlyCard card)
    {
        updateOrAnimatePosition(card, false);
    }

    private void updateOrAnimatePosition(ReadOnlyCard card, boolean update)
    {
        JScalableImage jcard = Services.get(IScalableImageManager.class).getJScalableImage(card.getId());
        ReadOnlyCardStack cardStack = card.getCardStack();
        int layerIndex = LAYER_CARDS + 20 * cardStack.getTypeNumber() + card.getCardIndex();        
        Rectangle rect_dst = Services.get(IPositionManager.class).getBounds(card.getId());
        
        if (update)
        {
            gamePanel.setLayer(jcard, layerIndex);
            jcard.setBounds(rect_dst.x, rect_dst.y, rect_dst.width, rect_dst.height);
        }
        else
        {
            MovingAnimation anim_pos = new MovingAnimation(jcard.getLocation(), rect_dst.getLocation());
            animator.animate(jcard, new AnimationEnd(false, layerIndex), ANIMATION_TIME_CARD , anim_pos);
        }
    }

    @Override
    public void updatePosition(ReadOnlyCardStack cardStack)
    {
        JScalableImage jcardstack = Services.get(IScalableImageManager.class).getJScalableImage(cardStack.getId());
        Rectangle rect_dst = Services.get(IPositionManager.class).getBounds(cardStack.getId());

        int layerIndex = LAYER_CARDSTACKS;
        gamePanel.setLayer(jcardstack, layerIndex);

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
        JLabel label = new JLabel("+" + incr);
        
        
        label.setFont(scoreFont);

        label.setSize(label.getPreferredSize());
        gamePanel.add(label);
        gamePanel.setLayer(label, LAYER_ANIMATIONS);
        
        // determine final card position (the card itself may still be animated into its final position)
        // to calculate the animation's start position
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        Rectangle rect_dst = Services.get(IPositionManager.class).getBounds(cardId);
        int posx = rect_dst.x + (rect_dst.width - label.getWidth()) / 2;
        int posy = rect_dst.y;        
        
        IAnimation anim_color = new ColorAnimation(new Color(50, 100, 50, 255), new Color(255, 165, 0, 0));
        IAnimation anim_pos = new MovingAnimation(posx, posy, posx, posy - 100);

        animator.animate(label, new AnimationEnd(true, -1), ANIMATION_TIME_SCORE, anim_color, anim_pos);
    }
}
