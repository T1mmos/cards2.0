package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.services.context.ContextType;

public class A_DebugDrawDebugLines extends ActionBase
{
    protected A_DebugDrawDebugLines()
    {
        super(Actions.ACTION_DEBUG_DRAWOUTLINES, Loc.get(LocKey.DebugMenu_drawdebuglines));
    }

    @Override
    public void onContextInitialized(ContextType type)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onContextDropped(ContextType type)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onChange(ReadOnlyChange roChange)
    {
        // TODO Auto-generated method stub
        
    }

}
