package gent.timdemey.cards.services.frame;


import gent.timdemey.cards.readonlymodel.IStateListener;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.services.interfaces.ISoundService;

public class FrameStateListener implements IStateListener
{
    private final ISoundService _SoundService;
    private final IFrameService _FrameService;
    private final IPanelService _PanelService;
    private final Context _Context;
    
    public FrameStateListener(
        Context context,
        ISoundService soundService,
        IFrameService frameService,
        IPanelService panelService)
    {
        this._Context = context;
        this._SoundService = soundService;
        this._FrameService = frameService;
        this._PanelService  = panelService;
    }
    
    @Override
    public void onChange(ReadOnlyChange change)
    {
        ReadOnlyState state = _Context.getReadOnlyState();

        CheckSound(state, change);
    }
    
    private void CheckSound(ReadOnlyState state, ReadOnlyChange change)
    {        
        if (change.property == ReadOnlyState.CardGame)
        {
            ReadOnlyCardGame cardGame = state.getCardGame();
            if (cardGame == null)
            {
                _FrameService.showPanel(PanelDescriptors.Menu);
                _FrameService.removePanel(PanelDescriptors.Game);
            }
            else
            {             
                _FrameService.showPanel(PanelDescriptors.Game);
                _PanelService.createComponentsAsync(this::onCreatedComponents);                
                _SoundService.play(ResourceDescriptors.SoundTest);
            }
        }
        else if (change.property == ReadOnlyCardStack.Cards)
        {
            if (change.addedValues != null && !change.addedValues.isEmpty())
            {
                _SoundService.play(ResourceDescriptors.SoundPutDown);
            }
        }
    }
     
    private void onCreatedComponents ()
    {
        _PanelService.rescaleResourcesAsync(this::onRescaledResources);
    }
    
    private void onRescaledResources ()
    {       
        _FrameService.hidePanel(PanelDescriptors.Load);
    }
}
