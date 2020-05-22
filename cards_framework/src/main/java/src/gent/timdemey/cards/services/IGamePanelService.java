package gent.timdemey.cards.services;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.gamepanel.IGamePanelReceiver;
import gent.timdemey.cards.services.scaleman.ImageDefinition;

public interface IGamePanelService
{
    public List<ImageDefinition> getScalableImageDefinitions();

    public void createGamePanel(int w, int h, IGamePanelReceiver callback);

    public void destroyGamePanel();

    public void relayout();

    public void rescaleAsync(Runnable callback);

    public void setDrawDebug(boolean on);

    public boolean getDrawDebug();

    public void setVisible(ReadOnlyCard card, boolean visible);

    public void animatePosition(ReadOnlyCard card);

    public void animatePlayerScore(UUID playerId, int oldScore, int newScore);

    public void animateCardScore(UUID cardId, int oldValue, int newValue);
}
