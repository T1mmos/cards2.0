package gent.timdemey.cards.services.gamepanel;

import java.util.List;

import gent.timdemey.cards.entities.E_Card;
import gent.timdemey.cards.entities.E_CardStack;
import gent.timdemey.cards.services.scaleman.ImageDefinition;

public interface IGamePanelManager {
    public List<ImageDefinition> getScalableImageDefinitions();

    public void createGamePanel(int w, int h, IGamePanelReceiver callback);

    public void destroyGamePanel();

    public void relayout();

    public void updateScalableImages(Runnable callback);

    public void setDrawDebug(boolean on);

    public boolean getDrawDebug();

    public void setVisible(E_Card card, boolean visible);
    
    public void updatePosition(E_Card card);
    
    public void updatePosition(E_CardStack cardStack);
    
    public void animatePosition(E_Card card);

}
