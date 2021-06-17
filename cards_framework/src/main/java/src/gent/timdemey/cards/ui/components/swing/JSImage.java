package gent.timdemey.cards.ui.components.swing;

import javax.swing.JPanel;

import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.ext.IHasComponent;

public final class JSImage extends JPanel implements IHasComponent, IHasDrawer<JPanel>
{
    private IDrawer<JPanel> drawer;
    private IComponent scomp;

    JSImage()
    {

    }

    @Override
    public final IDrawer<JPanel> getDrawer()
    {
        return drawer;
    }

    @Override
    public void setDrawer(IDrawer<JPanel> drawer)
    {
        this.drawer = drawer;
    }

    @Override
    public final IComponent getComponent()
    {
        return scomp;
    }

    @Override
    public final void setSComponent(IComponent scomp)
    {
        this.scomp = scomp;
    }
}
