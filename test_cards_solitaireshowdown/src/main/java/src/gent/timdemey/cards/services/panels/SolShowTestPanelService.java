package gent.timdemey.cards.services.panels;

import java.util.Arrays;
import java.util.List;

import gent.timdemey.cards.ui.actions.Actions;
import gent.timdemey.cards.ui.actions.SolShowTestActions;

public class SolShowTestPanelService extends SolShowPanelService
{

    @Override
    protected List<String> getMenuActionDefs()
    {
        return Arrays.asList(SolShowTestActions.ACTION_FAKESOLSHOWGAME, Actions.ACTION_QUIT);
    }
}
