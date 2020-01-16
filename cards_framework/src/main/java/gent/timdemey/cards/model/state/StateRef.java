package gent.timdemey.cards.model.state;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.IChangeTracker;

public abstract class StateRef
{
    public final Property property;
    public final UUID entityId;

    protected StateRef(Property property, UUID entityId)
    {
        this.property = property;
        this.entityId = entityId;
    }

    protected final IChangeTracker getChangeTracker()
    {
        IContextService contextService = Services.get(IContextService.class);
        IChangeTracker changeTracker = contextService.getThreadContext().getChangeTracker();
        return changeTracker;
    }
}
