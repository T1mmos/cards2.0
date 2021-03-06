package gent.timdemey.cards.services.panels;

import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.panels.game.SolitaireGamePanelManager;
import gent.timdemey.cards.ui.panels.PanelService;

public class SolitairePanelService extends PanelService
{    
    @Override
    protected void addAbsentPanelManagers()
    {
        // install 
        addPanelManagerIfAbsent(PanelDescriptors.Game, () -> new SolitaireGamePanelManager());
                
        // super class installs the rest
        super.addAbsentPanelManagers();
    }
}
