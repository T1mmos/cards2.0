package gent.timdemey.cards.ui.actions;

import gent.timdemey.cards.localization.Loc;
import gent.timdemey.cards.localization.LocKey;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.ContextType;

public class A_StartMultiplayerGame extends ActionBase
{
    protected A_StartMultiplayerGame()
    {
        super(Actions.ACTION_STARTMULTIPLAYER, Loc.get(LocKey.Button_startMultiplayerGame));
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
        if (roChange.property == ReadOnlyState.Players)
        {
            checkEnabled();
        }
    }
}
