package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_ShowToggleMenuMP;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IFrameService;

public class C_ShowToggleMenuMP extends DialogCommandBase
{
    private final IFrameService _FrameService;
    
    public C_ShowToggleMenuMP(
        Container container, IFrameService frameService, 
        P_ShowToggleMenuMP parameters)
    {
        super(container, parameters);
        
        this._FrameService = frameService;
    }
    
    @Override
    protected CanExecuteResponse canShowDialog()
    {
        CheckContext(ContextType.UI);
                
        // you can always hide this dialog when it's shown
        if (_FrameService.isShown(PanelDescriptors.GameMenu))
        {
            return CanExecuteResponse.yes();
        }
        
        // you can only make it visible when the game is started
        if (_State.getGameState() != GameState.Started)
        {
            return CanExecuteResponse.no("State.GameState should be Started in order to show the in-game menu");
        }

        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog()
    {
        if (_FrameService.isShown(PanelDescriptors.GameMenu))
        {
            _FrameService.hidePanel(PanelDescriptors.GameMenu);
        }
        else
        {
            _FrameService.showPanel(PanelDescriptors.GameMenu, null, null);    
        }
    }

}
