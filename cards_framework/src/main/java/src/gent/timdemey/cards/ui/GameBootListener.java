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
        IPanelService panelServ = Services.get(IPanelService.class);

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
                frameServ.showPanel(PanelDescriptors.Game);
                panelServ.createComponentsAsync(GameBootListener::onCreatedComponents);
            }
        }
    }
            
    private static void onCreatedComponents ()
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        panelServ.rescaleResourcesAsync(GameBootListener::onRescaledResources);
    }
    
    private static void onRescaledResources ()
    {
        IFrameService frameServ = Services.get(IFrameService.class);        
        frameServ.removePanel(PanelDescriptors.Load);
    }
}
