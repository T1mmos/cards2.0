package gent.timdemey.cards.services.panels;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;

import gent.timdemey.cards.services.contract.RescaleRequest;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import net.miginfocom.swing.MigLayout;

public class LoadPanelManager extends PanelManagerBase
{
    private PanelBase loadPanel;
    
    @Override
    public boolean isCreated()
    {
        return loadPanel != null;
    }

    @Override
    public JComponent getOrCreate()
    {
        loadPanel = new PanelBase(PanelDescriptors.LOAD);
        loadPanel.setOpaque(false); 
        loadPanel.setLayout(new MigLayout("insets 0, align 50% 50%"));
        loadPanel.add(new JLabel("LOADING..."));
        
        return loadPanel;
    }
    
    @Override
    public void onShown()
    {
    }
   

    @Override
    public void onHidden()
    {
        
    }


    @Override
    public void preload()
    {
        
    }

    @Override
    public void destroy()
    {
        loadPanel = null;
    }

    @Override
    public void createRescaleRequests(List<? super RescaleRequest> requests)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void createScalableComponents()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void positionScalableComponents()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onResourcesRescaled()
    {
        // TODO Auto-generated method stub
        
    }


}
