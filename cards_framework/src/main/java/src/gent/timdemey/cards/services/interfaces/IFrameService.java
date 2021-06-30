package gent.timdemey.cards.services.interfaces;

import java.awt.GraphicsDevice;
import java.util.function.Consumer;

import javax.swing.JFrame;

import gent.timdemey.cards.services.contract.SnapSide;
import gent.timdemey.cards.services.contract.descriptors.DataPanelDescriptor;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.ui.panels.PanelOutData;

public interface IFrameService
{
    public JFrame getFrame();

    public boolean isShown(PanelDescriptor desc);
    public void showPanel(PanelDescriptor desc);
    public <IN, OUT> void showPanel(DataPanelDescriptor<IN, OUT> desc, IN data, Consumer<PanelOutData<OUT>> onClose);
    public void showMessage(String title, String message);
    public void showInternalError();
    public void removePanel(PanelDescriptor desc);

    public void maximize();
    public void minimize();

    public boolean isSnapped();
    public boolean isSnapped(SnapSide ... snapsides);
    public void snap(SnapSide ... snapsides);
    public void snap(GraphicsDevice device, SnapSide ... snapsides);
    public void unsnap();
    
    public void setDrawDebug(boolean on);
    public boolean getDrawDebug();

    public void setLocation(int x, int y);
    public void setBounds(int x, int y, int w, int h);
    public void updatePositionService();
}
