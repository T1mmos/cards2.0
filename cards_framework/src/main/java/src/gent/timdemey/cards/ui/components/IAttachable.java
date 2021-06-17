package gent.timdemey.cards.ui.components;

import javax.swing.JComponent;

public interface IAttachable<T extends JComponent>
{
    public void onAttached(T comp);
    public T getJComponent();
}
