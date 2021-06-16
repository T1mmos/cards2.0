package gent.timdemey.cards.services.panels;

import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.panels.game.SolShowGamePanelManager;
import gent.timdemey.cards.ui.panels.PanelService;

public class SolShowPanelService extends PanelService 
{
    @Override
    protected void addAbsentPanelManagers()
    {
        addPanelManagerIfAbsent(PanelDescriptors.Game, () -> new SolShowGamePanelManager());
        
        super.addAbsentPanelManagers();
    } 
}
