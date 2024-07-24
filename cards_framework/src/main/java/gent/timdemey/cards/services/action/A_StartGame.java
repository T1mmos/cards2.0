package gent.timdemey.cards.services.action;

import java.awt.event.ActionEvent;


import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IActionService;
import gent.timdemey.cards.services.interfaces.IFrameService;

class A_StartGame extends ActionBase
{
    private final IFrameService _FrameService;
    protected A_StartGame(
            IActionService actionService,  
            IFrameService frameService,
            ActionDescriptor desc, 
            String title)
    {
        super(actionService, desc, title);
        
        this._FrameService = frameService;
    }

    @Override
    public void onChange(ReadOnlyChange roChange)
    {
        if (roChange.property == ReadOnlyState.CardGame)
        {
            checkEnabled();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        _FrameService.showPanel(PanelDescriptors.Load);
        
        super.actionPerformed(e);
    }
}
