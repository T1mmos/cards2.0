package gent.timdemey.cards.ui.components.swing;

import javax.swing.JPanel;

import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.ComponentBase;
import gent.timdemey.cards.ui.components.ext.IHasComponent;

public final class JSImage extends JPanel implements IHasComponent<ComponentBase>, IHasDrawer<JPanel>
{
    private IDrawer<JPanel> drawer;
    private ComponentBase comp;

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
    public final ComponentBase getComponent()
    {
        return comp;
    }

    @Override
    public final void setComponent(ComponentBase scomp)
    {
        this.comp = scomp;
    }
}
