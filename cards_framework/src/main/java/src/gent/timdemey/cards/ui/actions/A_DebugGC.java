package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.services.context.ContextType;

public class A_DebugGC extends ActionBase
{
    protected A_DebugGC(ActionDescriptor desc, String title)
    {
        super(desc, title);
    }

    @Override
    public void onContextInitialized(ContextType type)
    {
        
    }

    @Override
    public void onContextDropped(ContextType type)
    {
        
    }

    @Override
    public void onChange(ReadOnlyChange roChange)
    {
        
    }
}
