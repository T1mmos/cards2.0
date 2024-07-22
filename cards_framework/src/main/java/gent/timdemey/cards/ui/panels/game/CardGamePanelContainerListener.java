package gent.timdemey.cards.ui.panels.game;

import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;

import javax.swing.JComponent;

import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.interfaces.IPanelService;
import gent.timdemey.cards.ui.panels.IPanelManager;

public class CardGamePanelContainerListener implements ContainerListener
{

    private final IPanelService _PanelService;
    public CardGamePanelContainerListener (IPanelService panelService)
    {
        this._PanelService = panelService;
    }

    @Override
    public void componentAdded(ContainerEvent e)
    {
        JComponent jcomp = (JComponent) e.getChild();
        
        IPanelManager pm = _PanelService.getPanelManager(PanelDescriptors.Game);
        pm.positionComponent(jcomp);
    }

    @Override
    public void componentRemoved(ContainerEvent e)
    {
        
    }
    

    
}
