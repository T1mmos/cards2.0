package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.IPreload;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.services.gamepanel.GamePanel;
import gent.timdemey.cards.services.scaleman.IScalableComponent;

public interface IGamePanelService extends IPreload
{
    public GamePanel createGamePanel();
    public void fillGamePanel();
    public void destroyGamePanel();

    public void relayout();
    public void rescaleAsync();
    public void setDrawDebug(boolean on);
    public boolean getDrawDebug();

    public void animateCard(ReadOnlyCard card);    
    public void animateCardScore(ReadOnlyCard card, int oldValue, int newValue);

    public int getLayer(IScalableComponent<?> scalableComponent);
    public void setLayer(IScalableComponent<?> component, int layerIndex);    
    
    public void add(IScalableComponent<?> comp);
    public void remove(IScalableComponent<?> comp);
}
