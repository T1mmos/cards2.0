package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.IPreload;
import gent.timdemey.cards.services.gamepanel.GamePanel;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public interface IGamePanelService extends IPreload
{
    public GamePanel createGamePanel();
    public void fillGamePanel();
    public void destroyGamePanel();

    public void relayout();
    public void rescaleAsync();
    public void setDrawDebug(boolean on);
    public boolean getDrawDebug();

    public void startAnimation(IScalableComponent scaleComp);
    public void stopAnimation(IScalableComponent scaleComp);

    public int getLayer(IScalableComponent scaleComp);
    public void setLayer(IScalableComponent scaleComp, int layerIndex);    
    
    public void add(IScalableComponent comp);
    public void remove(IScalableComponent comp);
    public void updateComponent(IScalableComponent comp);
}
