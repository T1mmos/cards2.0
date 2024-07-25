package gent.timdemey.cards.model.entities.commands;


import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.state.GameState;
import gent.timdemey.cards.model.entities.state.State;
import gent.timdemey.cards.services.context.Context;
import gent.timdemey.cards.services.context.ContextType;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IFrameService;

public class D_ToggleMenuMP extends DialogCommandBase
{

    @Override
    protected CanExecuteResponse canShowDialog(Context context, ContextType type, State state)
    {
        CheckContext(type, ContextType.UI);
        
        IFrameService frameServ = Services.get(IFrameService.class);
        
        // you can always hide this dialog when it's shown
        if (frameServ.isShown(PanelDescriptors.GameMenu))
        {
            return CanExecuteResponse.yes();
        }
        
        // you can only make it visible when the game is started
        if (state.getGameState() != GameState.Started)
        {
            return CanExecuteResponse.no("State.GameState should be Started in order to show the in-game menu");
        }

        return CanExecuteResponse.yes();
    }

    @Override
    protected void showDialog(Context context, ContextType type, State state)
    {
        IFrameService frameServ = Services.get(IFrameService.class);
        if (frameServ.isShown(PanelDescriptors.GameMenu))
        {
            frameServ.hidePanel(PanelDescriptors.GameMenu);
        }
        else
        {
            frameServ.showPanel(PanelDescriptors.GameMenu, null, null);    
        }
            
    }

}
