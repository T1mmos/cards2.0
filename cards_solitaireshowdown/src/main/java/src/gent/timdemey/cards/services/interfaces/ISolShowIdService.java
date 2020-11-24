package gent.timdemey.cards.services.interfaces;

import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;

public interface ISolShowIdService extends IIdService
{
    public UUID createSpecialCounterComponentId(ReadOnlyCardStack cs);

    public UUID createSpecialBackgroundResourceId();
    public UUID createSpecialBackgroundComponentId(ReadOnlyCardStack cs);
    public UUID createPlayerNameComponentId(ReadOnlyPlayer player);
    public UUID createPlayerBgComponentId(boolean remote);
    public UUID createCardAreaBgComponentId(boolean remote);
}
