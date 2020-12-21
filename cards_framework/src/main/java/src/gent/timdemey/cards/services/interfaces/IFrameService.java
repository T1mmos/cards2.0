package gent.timdemey.cards.services.interfaces;

import javax.swing.JFrame;

import gent.timdemey.cards.services.contract.descriptors.DataPanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.panels.PanelInData;
import gent.timdemey.cards.services.panels.PanelOutData;

public interface IFrameService
{
    public JFrame getFrame();
     
    // public void addPanel(PanelDescriptor desc);
    public void showPanel(PanelDescriptor desc);
    public <IN, OUT> PanelOutData<OUT> showPanel(DataPanelDescriptor<IN, OUT> desc, PanelInData<IN> data);
    public void hidePanel(PanelDescriptor desc);
    public void removePanel(PanelDescriptor desc);
    
    public void maximize();
    public void minimize();
    public void unmaximize();
    
    public void setDrawDebug(boolean on);
    public boolean getDrawDebug();

    public void setLocation(int x, int y);
    public void setBounds(int x, int y, int w, int h);
}
