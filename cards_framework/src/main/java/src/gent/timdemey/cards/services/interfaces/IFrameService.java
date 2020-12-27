package gent.timdemey.cards.services.interfaces;

import java.util.function.Consumer;

import javax.swing.JFrame;

import gent.timdemey.cards.services.contract.descriptors.DataPanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.panels.PanelOutData;

public interface IFrameService
{
    public JFrame getFrame();
     
    public void showPanel(PanelDescriptor desc);
    public <IN, OUT> void showPanel(DataPanelDescriptor<IN, OUT> desc, IN data, Consumer<PanelOutData<OUT>> onClose);
    public void showMessage(String title, String message);
    public void showInternalError();
    public void hidePanel(PanelDescriptor desc);
    public void removePanel(PanelDescriptor desc);   

    public void maximize();
    public void minimize();
    public void unmaximize();
    
    public void setDrawDebug(boolean on);
    public boolean getDrawDebug();

    public void setLocation(int x, int y);
    public void setBounds(int x, int y, int w, int h);

    public void updatePositionService();



}
