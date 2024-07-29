package gent.timdemey.cards.services.panels;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.panels.game.SolitaireGamePanelManager;
import gent.timdemey.cards.ui.panels.PanelService;

public class SolitairePanelService extends PanelService
{    

    public SolitairePanelService(Container container, IScalingService scalingService, IPanelService panelService)
    {
        super(container, scalingService, panelService);
    }
    
    @Override
    protected void addAbsentPanelManagers()
    {
        // install 
        addPanelManagerIfAbsent(PanelDescriptors.Game, () -> _Container.Get(SolitaireGamePanelManager.class));
                
        // super class installs the rest
        super.addAbsentPanelManagers();
    }
}
