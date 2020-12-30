package gent.timdemey.cards.services.panels.menu;

import java.util.Arrays;
import java.util.List;

import gent.timdemey.cards.ui.actions.ActionDescriptor;
import gent.timdemey.cards.ui.actions.ActionDescriptors;
import gent.timdemey.cards.ui.actions.SolShowTestActionDescriptors;

public class SolShowTestMenuPanelManager extends MenuPanelManager
{

    protected List<ActionDescriptor> getMenuActionDescriptors()
    {
        return Arrays.asList(SolShowTestActionDescriptors.ad_fakegame, ActionDescriptors.ad_quit);
    }
}
