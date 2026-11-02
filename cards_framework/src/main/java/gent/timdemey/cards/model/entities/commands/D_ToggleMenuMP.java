package gent.timdemey.cards.model.entities.commands;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.model.entities.commands.contract.CanExecuteResponse;
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
        
        IFrameService frameServ = Services.get(IFrameService.class);
        
        // you can always hide this dialog when it's shown
        if (frameServ.isShown(PanelDescriptors.GameMenu))
        {
            return CanExecuteResponse.yes();
        }
        
        // you can only make it visible while a game is ongoing
        if (state.getCardGame() == null)
        {
            return CanExecuteResponse.no("State.CardGame should be set in order to show the in-game menu");
        }

        // the in-game menu cannot be summoned on top of another dialog
        if (frameServ.isDialogShown())
        {
            return CanExecuteResponse.no("Another dialog is currently shown");
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
