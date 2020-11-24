package gent.timdemey.cards.services.interfaces;

import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;

public interface ISolShowIdService extends IIdService
{
    // components
    public UUID createSpecialCounterComponentId(ReadOnlyCardStack cs);
    public UUID createSpecialBackgroundComponentId(ReadOnlyCardStack cs);
    public UUID createPlayerNameComponentId(ReadOnlyPlayer player);    
    public UUID createPlayerBgComponentId(boolean remote);
    public UUID createCardAreaBgComponentId(boolean remote);
    
    // resources
    public UUID createSpecialBackgroundResourceId();
    public UUID createPlayerBgResourceId(boolean remote);
    public UUID createCardAreaBgResourceId(boolean remote);
}
