package gent.timdemey.cards.services.context;

import java.util.ConcurrentModificationException;

import gent.timdemey.cards.model.entities.commands.CommandBase;
import gent.timdemey.cards.model.state.State;

public class MockContextService extends ContextService
{
    @Override
    public boolean isUiThread()
    {
        return true;
    }
    
    @Override
    public void initialize(ContextType type)
    {
        if (type == ContextType.UI)
        {
            super.initialize(type);
            return;
        }
        if (type == ContextType.Client)
        {
            Context context = Context.createContext(type, new MockCommandExecutor(), false);
            Context prev = fullContexts.putIfAbsent(type, context);
            if(prev != null)
            {
                throw new ConcurrentModificationException("Context concurrently installed by different thread for type " + type);
            }
        }
    }
}
