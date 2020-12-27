package gent.timdemey.cards.ui;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;

public class GameBootListener implements IStateListener
{
    GameBootListener()
    {
    }

    @Override
    public void onChange(ReadOnlyChange change)
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();

        if (change.property == ReadOnlyState.CardGame)
        {
            IFrameService frameServ = Services.get(IFrameService.class);

            ReadOnlyCardGame cardGame = state.getCardGame();
            if (cardGame == null)
            {
                frameServ.showPanel(PanelDescriptors.MENU);
                frameServ.removePanel(PanelDescriptors.GAME);
                frameServ.removePanel(PanelDescriptors.LOAD);
            }
            else
            {
                frameServ.showPanel(PanelDescriptors.GAME);
                frameServ.showPanel(PanelDescriptors.LOAD);              
                
                // and now we can start rescaling the resources according to the
                // dimensions
                IPanelService panelServ = Services.get(IPanelService.class);
                panelServ.rescaleResourcesAsync(GameBootListener::onRescaledResources);
            }
        }
    }
    
    private static void onRescaledResources ()
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        IFrameService frameServ = Services.get(IFrameService.class);
        
        // add different top-level panels (e.g. menu, game, overlay, ...)        
        for (PanelDescriptor pd : panelServ.getPanelDescriptors())
        {
        //    frameServ.addPanel(pd);
        }
        
        // the resources have loaded and are rescaled, so create and position 
        // the components that use them
        panelServ.createScalableComponents();
        panelServ.positionScalableComponents();
        
        // finally bring the main panel alive
        frameServ.getFrame().setVisible(true);
    }
}
