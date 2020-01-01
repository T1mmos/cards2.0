package gent.timdemey.cards.services;

import java.util.List;

import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.gamepanel.IGamePanelReceiver;
import gent.timdemey.cards.services.scaleman.ImageDefinition;

public interface IGamePanelManager {
    public List<ImageDefinition> getScalableImageDefinitions();

    public void createGamePanel(int w, int h, IGamePanelReceiver callback);

    public void destroyGamePanel();

    public void relayout();

    public void updateScalableImages(Runnable callback);

    public void setDrawDebug(boolean on);

    public boolean getDrawDebug();

    public void setVisible(ReadOnlyCard card, boolean visible);
    
    public void updatePosition(ReadOnlyCard card);
    
    public void updatePosition(ReadOnlyCardStack cardStack);
    
    public void animatePosition(ReadOnlyCard card);

}
