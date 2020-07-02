package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;

public interface ISolShowGamePanelService extends IGamePanelService
{
    public void animateCardScore(ReadOnlyCard card, int oldScore, int newScore);
    public void animateSpecialScore(ReadOnlyCardStack cardStack);
}
