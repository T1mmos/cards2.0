package gent.timdemey.cards.ui.components.drawers;

import javax.swing.JComponent;

public interface IHasDrawer<T extends JComponent>
{
    IDrawer<T> getDrawer();
    void setDrawer(IDrawer<T> drawer);
}
