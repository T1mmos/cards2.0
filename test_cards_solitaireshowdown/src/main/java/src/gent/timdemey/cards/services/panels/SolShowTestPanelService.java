package gent.timdemey.cards.services.panels;

import java.util.Arrays;
import java.util.List;

import gent.timdemey.cards.ui.actions.ActionDescriptor;
import gent.timdemey.cards.ui.actions.SolShowTestActionDescriptors;

public class SolShowTestPanelService extends SolShowPanelService
{
    @Override
    protected List<ActionDescriptor> getMenuActionDescriptors()
    {
        return Arrays.asList(SolShowTestActionDescriptors.ad_fakegame);
    }
}
