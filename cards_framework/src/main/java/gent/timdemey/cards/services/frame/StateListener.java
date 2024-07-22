package gent.timdemey.cards.services.frame;


import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.ISoundService;

public class StateListener implements IStateListener
{        
    @Override
    public void onChange(ReadOnlyChange change)
    {
        IContextService contextService = Services.get(IContextService.class);
        Context context = contextService.getThreadContext();
        ReadOnlyState state = context.getReadOnlyState();

        CheckSound(state, change);
    }
    
    private void CheckSound(ReadOnlyState state, ReadOnlyChange change)
    {
        ISoundService soundServ = Services.get(ISoundService.class);
        IFrameService frameServ = Services.get(IFrameService.class);
        IPanelService panelServ = Services.get(IPanelService.class);
        
        if (change.property == ReadOnlyState.CardGame)
        {
            ReadOnlyCardGame cardGame = state.getCardGame();
            if (cardGame == null)
            {
                frameServ.showPanel(PanelDescriptors.Menu);
                frameServ.removePanel(PanelDescriptors.Game);
            }
            else
            {             
                frameServ.showPanel(PanelDescriptors.Game);
                panelServ.createComponentsAsync(StateListener::onCreatedComponents);
                
                soundServ.play(ResourceDescriptors.SoundTest);
            }
        }
        else if (change.property == ReadOnlyCardStack.Cards)
        {
            if (change.addedValues != null && !change.addedValues.isEmpty())
            {
                soundServ.play(ResourceDescriptors.SoundPutDown);
            }
        }
    }
     
    private static void onCreatedComponents ()
    {
        IPanelService panelServ = Services.get(IPanelService.class);
        panelServ.rescaleResourcesAsync(StateListener::onRescaledResources);
    }
    
    private static void onRescaledResources ()
    {
        IFrameService frameServ = Services.get(IFrameService.class);        
        frameServ.hidePanel(PanelDescriptors.Load);
    }
}
