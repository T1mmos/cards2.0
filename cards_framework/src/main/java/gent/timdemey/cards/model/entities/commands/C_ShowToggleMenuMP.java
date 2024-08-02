package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.commands.payload.P_ShowToggleMenuMP;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IFrameService;

public class C_ShowToggleMenuMP extends DialogCommandBase
{
    private final IFrameService _FrameService;
    
    public C_ShowToggleMenuMP(
        IContextService contextService, IFrameService frameService, State state,
        P_ShowToggleMenuMP parameters)
    {
        super(contextService, state, parameters);
        
        this._FrameService = frameService;
    }
    
    @Override
    protected CanExecuteResponse canShowDialog(Context context, ContextType type)
    {
        CheckContext(type, ContextType.UI);
                
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
    protected void showDialog(Context context, ContextType type)
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
