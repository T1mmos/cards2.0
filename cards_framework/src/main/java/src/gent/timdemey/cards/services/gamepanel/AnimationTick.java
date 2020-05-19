package gent.timdemey.cards.services.gamepanel;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.Timer;

import com.google.common.base.Preconditions;

class AnimationTick implements ActionListener
{
    private static final class AnimInfo
    {
        private long tickStart;
        private Point startPos;
        private Point endPos;
        private int animationTime;
    }

    private final Map<JComponent, AnimInfo> animations;
    private Timer timer;

    AnimationTick()
    {
        animations = new HashMap<>();
    }

    void setTimer(Timer timer)
    {
        Preconditions.checkNotNull(timer);
        this.timer = timer;
    }

    void addComponent(JComponent comp, Point dst, int animationTime)
    {
        AnimInfo animInfo = new AnimInfo();
        animInfo.startPos = new Point(comp.getX(), comp.getY());
        animInfo.endPos = dst;
        animInfo.tickStart = System.currentTimeMillis();
        animInfo.animationTime = animationTime;
        animations.put(comp, animInfo);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Preconditions.checkArgument(e.getSource() instanceof Timer);

       // ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState()
       //         .getCardGame();

        long currTickTime = System.currentTimeMillis();
        for (JComponent comp : new HashSet<>(animations.keySet()))
        {
            AnimInfo animInfo = animations.get(comp);
            long dt = Math.min(animInfo.animationTime, currTickTime - animInfo.tickStart);
            
            int x = animInfo.startPos.x + (int) (1.0 * dt * (animInfo.endPos.x - animInfo.startPos.x) / animInfo.animationTime);
            int y = animInfo.startPos.y + (int) (1.0 * dt * (animInfo.endPos.y - animInfo.startPos.y) / animInfo.animationTime);

            comp.setBounds(x, y, comp.getWidth(), comp.getHeight());

            if (x == animInfo.endPos.x && y == animInfo.endPos.y)
            {
                animations.remove(comp);
               // UUID id = Services.get(IScalableImageManager.class).getUUID(jcard);
               // ReadOnlyCard card = cardGame.getCard(id);
               // Services.get(IGamePanelManager.class).updatePosition(card);
            }
        }

        if (animations.isEmpty())
        {
            timer.stop();
        }
    }

}
