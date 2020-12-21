package gent.timdemey.cards.services.panels;

import java.util.EnumSet;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class MessagePanelManager extends DataPanelManagerBase<String, Void>
{
    @Override
    public Void onClose(PanelButtonType dbType)
    {
        return null;
    }

    @Override
    public void onShown()
    {

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
    public JComponent getOrCreate()
    {
        JPanel content = new JPanel(new MigLayout("insets 0"));

        content.add(new JLabel(inData.data_in), "push, wrap");

        return content;
    }


    @Override
    public void onHidden()
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void destroy()
    {
        // TODO Auto-generated method stub
        
    }

  
}
