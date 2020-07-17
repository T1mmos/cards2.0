package gent.timdemey.cards.services.interfaces;

import java.util.UUID;

import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;

public interface ISolShowIdService extends IIdService
{
    public UUID createSpecialCounterComponentId(ReadOnlyCardStack cs);

    public UUID createSpecialBackgroundResourceId();
    public UUID createSpecialBackgroundComponentId(ReadOnlyCardStack cs);
}
