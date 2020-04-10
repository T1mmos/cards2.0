package gent.timdemey.cards.test.mocks;

import gent.timdemey.cards.services.context.ContextService;

public class MockContextService extends ContextService
{
    @Override
    public boolean isUiThread()
    {
        return true;
    }
    
}
