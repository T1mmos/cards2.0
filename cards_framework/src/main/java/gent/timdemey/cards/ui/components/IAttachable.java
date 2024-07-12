package gent.timdemey.cards.ui.components;

import javax.swing.JComponent;

public interface IAttachable
{
    public void onAttached(JComponent comp);
    public JComponent getJComponent();
}
