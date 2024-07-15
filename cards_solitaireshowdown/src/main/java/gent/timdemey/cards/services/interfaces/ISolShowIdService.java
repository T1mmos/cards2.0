package gent.timdemey.cards.services.interfaces;

import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyPlayer;

public interface ISolShowIdService extends IIdService
{
    // comp2jcomp
    public UUID createSpecialCounterComponentId(ReadOnlyCardStack cs);
    public UUID createSpecialBackgroundComponentId(ReadOnlyCardStack cs);
    public UUID createPlayerNameComponentId(ReadOnlyPlayer player);    
    public UUID createPlayerBgComponentId(boolean remote);
    public UUID createCardAreaBgComponentId(boolean remote);
    public UUID createVsComponentId();
    
    // resources
    public UUID createSpecialBackgroundResourceId(boolean remote);
    public UUID createPlayerBgResourceId(boolean remote);
    public UUID createCardAreaBgResourceId(boolean remote);
    public UUID createVsResourceId();
}
