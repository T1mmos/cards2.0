package gent.timdemey.cards.services.panels.dialogs.message;

import java.util.EnumSet;

import javax.swing.JLabel;

import gent.timdemey.cards.services.contract.descriptors.PanelDescriptors;
import gent.timdemey.cards.services.panels.DataPanelManagerBase;
import gent.timdemey.cards.services.panels.PanelButtonType;
import gent.timdemey.cards.ui.PanelBase;
import net.miginfocom.swing.MigLayout;

public class MessagePanelManager extends DataPanelManagerBase<MessagePanelData, Void>
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
    public boolean isPanelCreated()
    {
        return contentPanel != null;
    }
    
    @Override
    public PanelBase createPanel()
    {
        contentPanel = new PanelBase(PanelDescriptors.Message.id);
        contentPanel.setLayout(new MigLayout("insets 0"));
        contentPanel.add(new JLabel(inData.payload.message), "push, wrap");

        return contentPanel;
    }
    
    @Override
    public PanelBase getPanel()
    {
        return contentPanel;
    }

    @Override
    public void destroyPanel()
    {
        contentPanel = null;
    }

    @Override
    public String createTitle()
    {
        return inData.payload.title;
    }  
}
