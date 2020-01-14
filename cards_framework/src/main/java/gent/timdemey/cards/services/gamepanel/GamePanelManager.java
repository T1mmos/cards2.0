package gent.timdemey.cards.services.gamepanel;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.IGameOperationsService;
import gent.timdemey.cards.services.IGamePanelManager;
import gent.timdemey.cards.services.IPositionManager;
import gent.timdemey.cards.services.IScalableImageManager;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.scaleman.ImageDefinition;
import gent.timdemey.cards.services.scaleman.JScalableImage;

public class GamePanelManager implements IGamePanelManager {
        
    private static final String SCALEGROUP_CARDS = "SCALEGROUP_CARDS";
    private static final String FILENAME_BACKSIDE = "backside_bluegrad.png";
    
    private GamePanelResizeListener resizeListener;
    private CardDragListener dragListener;
    private IStateListener gameEventListener;

    private boolean drawDebug = false;    
    protected GamePanel gamePanel;
    
    private Timer timer;
    private AnimationTick animationTick;
        
    private GamePanelManager() 
    {
    }

    @Override
    public void createGamePanel(int w , int h, final IGamePanelReceiver callback) {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());

        if (gamePanel == null) {
            gamePanel = new GamePanel();
            gamePanel.setBounds(0, 0, w, h);

            addScalableImages();
                                    
            resizeListener = new GamePanelResizeListener();
            dragListener = new CardDragListener();
            gameEventListener = new GameStateListener();
            
            gamePanel.addComponentListener(resizeListener);
            gamePanel.addMouseMotionListener(dragListener);
            gamePanel.addMouseListener(dragListener);
            Services.get(IContextService.class).getThreadContext().getReadOnlyState().addStateListener(gameEventListener); 
            Services.get(IPositionManager.class).calculate(gamePanel.getWidth(), gamePanel.getHeight());
            relayout();
            
            updateScalableImages(() -> 
            {
                SwingUtilities.invokeLater(() -> callback.onPanelCreated(gamePanel));
            });
        }
    }

    protected void addScalableImages()
    {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();        
        
        List<ReadOnlyCard> cards = cardGame.getCards();
        for (int i = 0; i < cards.size(); i++) {
            ReadOnlyCard card = cards.get(i);             
            JScalableImage jscalable = Services.get(IScalableImageManager.class).getJScalableImage(card.getId());
            
            String filename = card.isVisible() ? getFilename(card) : FILENAME_BACKSIDE;
            Services.get(IScalableImageManager.class).setImage(card.getId(), filename);
            
            gamePanel.add(jscalable);
        }
    }
    
    @Override
    public void destroyGamePanel() {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());

        if (gamePanel != null)
        {
            gamePanel.removeComponentListener(resizeListener);
            gamePanel.removeMouseMotionListener(dragListener);
            gamePanel.removeMouseListener(dragListener);
        }
        
        Services.get(IGameOperationsService.class).removeGameEventListener(gameEventListener);        
        Services.get(IScalableImageManager.class).clearManagedObjects();
        resizeListener = null;
        dragListener = null;
        gamePanel = null;
        gameEventListener = null;
    }

    @Override
    public void relayout() {
        Context context = Services.get(IContextService.class).getThreadContext();
        ReadOnlyCardGame cardGame = context.getReadOnlyState().getCardGame();
        IPositionManager posMan = Services.get(IPositionManager.class);
        posMan.calculate(gamePanel.getWidth(), gamePanel.getHeight());
         
        for (ReadOnlyCardStack cardStack : cardGame.getCardStacks())
        {
            updatePosition(cardStack);
        }
        for (ReadOnlyCard card : cardGame.getCards())
        {
            updatePosition(card);
        }
       
        gamePanel.repaint();
    }

    @Override
    public void setDrawDebug(boolean on) {
        Preconditions.checkState(SwingUtilities.isEventDispatchThread());
        drawDebug = on;

        if (gamePanel != null) {
            gamePanel.repaint();
        }
    }

    @Override
    public boolean getDrawDebug() {
        return drawDebug;
    }

    @Override
    public final void updateScalableImages(Runnable callback) {
        
        updateScalableImages();

        Services.get(IScalableImageManager.class).apply(callback);
    }
    
    protected void updateScalableImages()
    {
        IPositionManager posMan = Services.get(IPositionManager.class);        
        {
            Rectangle rect = posMan.getCardSize();
            if (rect.width > 0 && rect.height > 0)
            {
                Services.get(IScalableImageManager.class).setSize(SCALEGROUP_CARDS, rect.width, rect.height);
            }
        }
        
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame(); 
        
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
    public List<ImageDefinition> getScalableImageDefinitions() {
        Context context = Services.get(IContextService.class).getThreadContext();
        ReadOnlyCardGame cardGame = context.getReadOnlyState().getCardGame();
        List<ImageDefinition> imgDefs = new ArrayList<>();

        // add card fronts
        
        List<ReadOnlyCard> cards = cardGame.getCards();
        
        for (ReadOnlyCard card : cards) {
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
        String full = suit + "_" + value + ".png";
        
        return full;
    }

    @Override
    public void setVisible(ReadOnlyCard card, boolean visible) {
        String whatToShow = visible ? getFilename(card) : FILENAME_BACKSIDE;
        Services.get(IScalableImageManager.class).setImage(card.getId(), whatToShow);
    }
    
    @Override
    public void updatePosition(ReadOnlyCard card) 
    {            
        JScalableImage jcard = Services.get(IScalableImageManager.class).getJScalableImage(card.getId());
        Rectangle rect_dst = Services.get(IPositionManager.class).getBounds(card);
        
        ReadOnlyCardStack cardStack = card.getCardStack();
       
        int layerIndex = 200 + 20*cardStack.getCardTypeNumber() + card.getCardIndex();
        gamePanel.setLayer(jcard, layerIndex);   
        
        jcard.setBounds(rect_dst.x, rect_dst.y, rect_dst.width, rect_dst.height);        
    }

    @Override
    public void updatePosition(ReadOnlyCardStack cardStack) 
    {    
        JScalableImage jcardstack = Services.get(IScalableImageManager.class).getJScalableImage(cardStack.getId());
        Rectangle rect_dst = Services.get(IPositionManager.class).getBounds(cardStack);
        
   //     int layerIndex = cardStack.getCardStackType().getOrder() + 20*cardStack.getCardStackIndex();
        int layerIndex = 0;
        gamePanel.setLayer(jcardstack, layerIndex);   
        
        jcardstack.setBounds(rect_dst.x, rect_dst.y, rect_dst.width, rect_dst.height);        
    }
    
    @Override
    public void animatePosition(ReadOnlyCard card) {
        JScalableImage jcard = Services.get(IScalableImageManager.class).getJScalableImage(card.getId());
        Rectangle rect_dst = Services.get(IPositionManager.class).getBounds(card);
       
        if (timer == null)
        {
            animationTick = new AnimationTick();                
            timer = new Timer(15, animationTick);
            animationTick.setTimer(timer);
        }
        
        animationTick.addCard(jcard, rect_dst);
        
        
        if (!timer.isRunning())
        {
            timer.start();
        }
    }
}
