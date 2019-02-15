package gent.timdemey.cards.services.gamepanel;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import javax.swing.Timer;

import com.google.common.base.Preconditions;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.entities.E_Card;
import gent.timdemey.cards.entities.E_CardGame;
import gent.timdemey.cards.entities.IContextProvider;
import gent.timdemey.cards.services.scaleman.IScalableImageManager;
import gent.timdemey.cards.services.scaleman.JScalableImage;

class AnimationTick implements ActionListener {

    private static final long ANIMATION_TIME = 150; // time in ms to have a card placed in position
    
    private static final class AnimInfo 
    {
        private long tickStart;
        private Point startPos;
        private Point endPos;
    }
    
    private final Map<JScalableImage, AnimInfo> animations;
    private Timer timer;
    
    AnimationTick() {
        animations = new HashMap<>();
    }
    
    void setTimer(Timer timer)
    {
        Preconditions.checkNotNull(timer);
        this.timer = timer;
    }

    void addCard(JScalableImage image, Rectangle dst)
    {
        AnimInfo animInfo = new AnimInfo();
        animInfo.startPos = new Point(image.getX(), image.getY());
        animInfo.endPos = new Point(dst.x, dst.y);
        animInfo.tickStart = System.currentTimeMillis();
        animations.put(image, animInfo);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Preconditions.checkArgument(e.getSource() instanceof Timer);
        
        E_CardGame cardGame = Services.get(IContextProvider.class).getThreadContext().getCardGameState().getCardGame();
        
        long currTickTime = System.currentTimeMillis();
        for (JScalableImage jcard : new HashSet<>(animations.keySet()))
        {
            AnimInfo animInfo = animations.get(jcard);
            long dt = Math.min(ANIMATION_TIME, currTickTime - animInfo.tickStart);
            
            int x = animInfo.startPos.x + (int) (1.0*dt * (animInfo.endPos.x - animInfo.startPos.x) / ANIMATION_TIME);
            int y = animInfo.startPos.y + (int) (1.0*dt * (animInfo.endPos.y - animInfo.startPos.y) / ANIMATION_TIME);
            
            jcard.setBounds(x, y, jcard.getWidth(), jcard.getHeight());
            
            if (x == animInfo.endPos.x && y == animInfo.endPos.y) 
            {
                animations.remove(jcard);
                UUID id = Services.get(IScalableImageManager.class).getUUID(jcard);
                E_Card card = cardGame.getCard(id);
                Services.get(IGamePanelManager.class).updatePosition(card);
            }
        }
        
        if (animations.isEmpty())
        {
            timer.stop();
        }        
    }

}
