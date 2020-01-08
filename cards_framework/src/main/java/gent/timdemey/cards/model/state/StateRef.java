package gent.timdemey.cards.model.state;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.IContextService;
import gent.timdemey.cards.services.context.IChangeTracker;

public abstract class StateRef
{    
    protected StateRef()
    {
    }
    
    protected final IChangeTracker getChangeTracker()
    {
        IContextService contextService = Services.get(IContextService.class);
        IChangeTracker changeTracker = contextService.getThreadContext().getChangeTracker();
        return changeTracker;
    }
}
