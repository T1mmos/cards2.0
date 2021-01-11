package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
import gent.timdemey.cards.model.entities.game.GameState;
import gent.timdemey.cards.model.state.State;
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
        if (frameServ.isShown(PanelDescriptors.GAME_MENU))
        {
            frameServ.removePanel(PanelDescriptors.GAME_MENU);
        }
        else
        {
            frameServ.showPanel(PanelDescriptors.GAME_MENU, null, null);    
        }
            
    }

}
