package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.services.context.ContextType;

public class A_DebugSwitchCardsVisible extends ActionBase
{
    public A_DebugSwitchCardsVisible()
    {
        super(SolShowTestActionDescriptors.ad_switchcvis, "Switch card visibility");
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
