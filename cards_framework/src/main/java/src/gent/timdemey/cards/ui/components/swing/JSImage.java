package gent.timdemey.cards.ui.components.swing;

import javax.swing.JPanel;

import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import gent.timdemey.cards.ui.components.ext.ImageComponent;

public final class JSImage extends JPanel implements IHasComponent<ImageComponent>, IHasDrawer<JPanel>
{
    private IDrawer<JPanel> drawer;
    private ImageComponent scomp;

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
    public final ImageComponent getComponent()
    {
        return scomp;
    }

    @Override
    public final void setComponent(ImageComponent scomp)
    {
        this.scomp = scomp;
    }
}
