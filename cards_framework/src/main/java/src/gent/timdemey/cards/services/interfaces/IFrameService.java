package gent.timdemey.cards.services.interfaces;

import javax.swing.JComponent;
import javax.swing.JFrame;

import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;

public interface IFrameService
{
    public JFrame getFrame();
     
    public void addPanel(PanelDescriptor pDesc, JComponent comp);
    public PanelDescriptor getCurrentPanel();
    public void setPanel(PanelDescriptor menu);

    public void setDrawDebug(boolean on);
    public boolean getDrawDebug();
}
