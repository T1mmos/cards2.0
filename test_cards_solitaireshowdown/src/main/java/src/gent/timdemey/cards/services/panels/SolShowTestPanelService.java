package gent.timdemey.cards.services.panels;

import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.panels.menu.SolShowTestMenuPanelManager;

public class SolShowTestPanelService extends SolShowPanelService
{
    @Override
    protected void addAbsentPanelManagers()
    {
        addPanelManagerIfAbsent(PanelDescriptors.MENU, () -> new SolShowTestMenuPanelManager());
        
        super.addAbsentPanelManagers();
    }
}
