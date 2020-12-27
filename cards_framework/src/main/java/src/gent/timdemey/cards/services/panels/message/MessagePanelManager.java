package gent.timdemey.cards.services.panels.message;

import java.util.EnumSet;

import javax.swing.JLabel;

import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.panels.DataPanelManagerBase;
import gent.timdemey.cards.services.panels.PanelBase;
import gent.timdemey.cards.services.panels.PanelButtonType;
import net.miginfocom.swing.MigLayout;

public class MessagePanelManager extends DataPanelManagerBase<String, Void>
{
    private PanelBase contentPanel;
    
    @Override
    public Void onClose(PanelButtonType dbType)
    {
        return null;
    }

    @Override
    public EnumSet<PanelButtonType> getButtonTypes()
    {
        return SET_OK;
    }

    @Override
    public boolean isButtonEnabled(PanelButtonType dbType)
    {
        return true;
    }

    @Override
    public boolean isCreated()
    {
        // we can't reuse a message panel
        return false;
    }
    
    @Override
    public PanelBase create()
    {
        contentPanel = new PanelBase(PanelDescriptors.MESSAGE);
        contentPanel.setLayout(new MigLayout("insets 0"));
        contentPanel.add(new JLabel(inData.data_in), "push, wrap");

        return contentPanel;
    }
    
    @Override
    public PanelBase get()
    {
        return contentPanel;
    }

    @Override
    public void destroy()
    {
        contentPanel = null;
    }  
}
