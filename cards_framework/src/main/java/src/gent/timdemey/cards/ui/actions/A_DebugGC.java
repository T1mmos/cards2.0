package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.services.context.ContextType;

public class A_DebugGC extends ActionBase
{
    protected A_DebugGC()
    {
        super(Actions.ACTION_DEBUG_GC, Loc.get(LocKey.DebugMenu_gc));
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
