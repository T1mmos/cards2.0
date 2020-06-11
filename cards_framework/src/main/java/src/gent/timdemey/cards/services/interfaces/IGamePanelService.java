package gent.timdemey.cards.services.interfaces;

import java.util.UUID;

import gent.timdemey.cards.IPreload;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.scaleman.IScalableComponent;

public interface IGamePanelService extends IPreload
{
    public void createGamePanel(int w, int h);
    public void destroyGamePanel();

    public void relayout();
    public void rescaleAsync();
    public void setDrawDebug(boolean on);
    public boolean getDrawDebug();

    public void animatePosition(ReadOnlyCard card);    
    public void animatePosition(ReadOnlyCardStack cardStack);
    public void animatePlayerScore(UUID playerId, int oldScore, int newScore);
    public void animateCardScore(UUID cardId, int oldValue, int newValue);

    public int getLayer(IScalableComponent scalableComponent);
    public void setLayer(IScalableComponent component, int layerIndex);    
    public int getZOrder(IScalableComponent scalableComponent);
    public void setZOrder(IScalableComponent component, int zorder);
}
