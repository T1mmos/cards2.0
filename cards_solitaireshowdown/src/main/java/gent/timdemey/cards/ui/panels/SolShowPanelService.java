package gent.timdemey.cards.ui.panels;

import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.ui.panels.game.SolShowGamePanelManager;

public class SolShowPanelService extends PanelService 
{

    public SolShowPanelService(Container container, IScalingService scalingService)
    {
        super(container, scalingService);
    }
    
    @Override
    protected void addAbsentPanelManagers()
    {
        addPanelManagerIfAbsent(PanelDescriptors.Game, () -> _Container.Get(SolShowGamePanelManager.class));
        
        super.addAbsentPanelManagers();
    } 
}
