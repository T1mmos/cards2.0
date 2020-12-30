package gent.timdemey.cards.services.panels;

import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.panels.game.SolShowGamePanelManager;

public class SolShowPanelService extends PanelService 
{
    @Override
    protected void addAbsentPanelManagers()
    {
        addPanelManagerIfAbsent(PanelDescriptors.GAME, () -> new SolShowGamePanelManager());
        
        super.addAbsentPanelManagers();
    } 
}
