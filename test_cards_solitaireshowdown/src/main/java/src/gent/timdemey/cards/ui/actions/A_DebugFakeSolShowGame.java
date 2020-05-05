package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.services.context.ContextType;

public class A_DebugFakeSolShowGame extends ActionBase
{
    public A_DebugFakeSolShowGame()
    {
        super(SolShowTestActions.ACTION_FAKESOLSHOWGAME, "Fake SolShow game");
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
