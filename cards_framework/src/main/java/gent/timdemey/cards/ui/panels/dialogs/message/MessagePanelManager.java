package gent.timdemey.cards.ui.panels.dialogs.message;

import gent.timdemey.cards.di.Container;
import java.util.List;

import javax.swing.JLabel;

import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.PanelButtonDescriptor;
import gent.timdemey.cards.ui.components.swing.JSLayeredPane;
import gent.timdemey.cards.ui.panels.DataPanelManagerBase;

public class MessagePanelManager extends DataPanelManagerBase<MessagePanelData, Void>
{
    private JSLayeredPane contentPanel;
    
    public MessagePanelManager(Container container)
    {
        super(container);
    }
    
    @Override
    public Void onClose(PanelButtonDescriptor dbType)
    {
        return null;
    }

    @Override
    public List<PanelButtonDescriptor> getButtonTypes()
    {
        return SET_OK;
    }

    @Override
    public boolean isButtonEnabled(PanelButtonDescriptor dbType)
    {
        return true;
    }

    @Override
    public boolean isPanelCreated()
    {
        return contentPanel != null;
    }
    
    @Override
    public JSLayeredPane createPanel()
    {
        contentPanel = _JSFactory.createLayeredPane(ComponentTypes.PANEL);
        contentPanel.add(new JLabel(inData.payload.message), "push, wrap");

        return contentPanel;
    }
    
    @Override
    public JSLayeredPane getPanel()
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
