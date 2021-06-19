package gent.timdemey.cards.ui.panels;

import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.ui.panels.game.SolShowGamePanelManager;

public class SolShowPanelService extends PanelService 
{
    @Override
    protected void addAbsentPanelManagers()
    {
        addPanelManagerIfAbsent(PanelDescriptors.Game, () -> new SolShowGamePanelManager());
        
        super.addAbsentPanelManagers();
    } 
}
