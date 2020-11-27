package gent.timdemey.cards.services.interfaces;

import java.util.List;

import javax.swing.JComponent;

import gent.timdemey.cards.IPreload;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.services.scaling.IScalableComponent;

public interface IPanelService extends IPreload
{
    public List<PanelDescriptor> getPanelDescriptors();
    public JComponent getPanel(PanelDescriptor desc);
    public void onPanelShown(PanelDescriptor desc);
    public void onPanelHidden(PanelDescriptor desc);
    public void destroyPanel(PanelDescriptor desc);

    public void relayout();
    public void rescaleAsync();

    public void startAnimation(IScalableComponent scaleComp);
    public void stopAnimation(IScalableComponent scaleComp);

    public int getLayer(IScalableComponent scaleComp);
    public void setLayer(IScalableComponent scaleComp, int layerIndex);    
    
    public void add(IScalableComponent comp);
    public void remove(IScalableComponent comp);
    public void updateComponent(IScalableComponent comp);
}
