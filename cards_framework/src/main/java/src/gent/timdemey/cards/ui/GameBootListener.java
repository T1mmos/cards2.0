    package gent.timdemey.cards.ui;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.Context;
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
        IFrameService frameServ = Services.get(IFrameService.class);
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();

        if (change.property == ReadOnlyState.CardGame)
        {
            
            ReadOnlyCardGame cardGame = state.getCardGame();
            if (cardGame == null)
            {
                frameServ.showPanel(PanelDescriptors.Menu);
            }
            else
            {      
                
                // before showing the gamepanel we must create its scalable components
                IPanelService panelServ = Services.get(IPanelService.class);
                panelServ.rescaleResourcesAsync(GameBootListener::onRescaledResources);
            }
        }
    }
    
    private static void onRescaledResources ()
    {
        IPanelService panelServ = Services.get(IPanelService.class);
                
        // the resources have loaded and are rescaled, so create and position 
        // the comp2jcomp that use them
        panelServ.createScalableComponents();
        panelServ.positionScalableComponents();
        

        IFrameService frameServ = Services.get(IFrameService.class);

        frameServ.removePanel(PanelDescriptors.Load);
        frameServ.showPanel(PanelDescriptors.Game);    
        
    }
}
