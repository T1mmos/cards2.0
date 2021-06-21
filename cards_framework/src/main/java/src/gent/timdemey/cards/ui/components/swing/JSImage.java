package gent.timdemey.cards.ui.components.swing;

import java.awt.Graphics;

import javax.swing.JPanel;

import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.ComponentBase;
import gent.timdemey.cards.ui.components.ext.IHasComponent;

public final class JSImage extends JPanel implements IHasComponent<ComponentBase>, IHasDrawer
{
    private IDrawer drawer;
    private ComponentBase comp;
    
    @Override
    protected void paintComponent(Graphics g)
    {        
        drawer.draw(g, super::paintComponent);
    }
    
    @Override
    protected void paintChildren(Graphics g)
    {
        drawer.drawChildren(g, super::paintChildren);
    }

    @Override
    public final IDrawer getDrawer()
    {
        return drawer;
    }

    @Override
    public void setDrawer(IDrawer drawer)
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
