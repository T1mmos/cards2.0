package gent.timdemey.cards.services.action;

import java.awt.event.ActionEvent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyChange;
import gent.timdemey.cards.readonlymodel.ReadOnlyState;
import gent.timdemey.cards.services.contract.descriptors.ActionDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IFrameService;

class A_StartGame extends ActionBase
{
    protected A_StartGame(ActionDescriptor desc, String title)
    {
        super(desc, title);
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
        IFrameService frameServ = Services.get(IFrameService.class);
        frameServ.showPanel(PanelDescriptors.Load);
        
        super.actionPerformed(e);
    }
}
