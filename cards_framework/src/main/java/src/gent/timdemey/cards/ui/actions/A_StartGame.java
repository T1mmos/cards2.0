package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.ContextType;

public class A_StartGame extends ActionBase
{
    protected A_StartGame(ActionDescriptor desc, String title)
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
        if (roChange.property == ReadOnlyState.CardGame)
        {
            checkEnabled();
        }
    }
}
