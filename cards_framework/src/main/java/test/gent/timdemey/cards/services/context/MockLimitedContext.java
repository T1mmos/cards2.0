package gent.timdemey.cards.services.context;

import gent.timdemey.cards.model.entities.commands.CommandBase;

public class MockLimitedContext extends LimitedContext
{

    public MockLimitedContext(ContextType contextType)
    {
        super(contextType, null);
    }
    
    @Override
    public void schedule(CommandBase command)
    {
        if (getContextType() == ContextType.Client)
        {
            // in unit testing we don't communicate to a server
            return;
        }
        
        super.schedule(command);
    }

}
