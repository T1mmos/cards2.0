package gent.timdemey.cards.ui.components.swing;

import java.awt.Graphics;

import javax.swing.JLayeredPane;

import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import gent.timdemey.cards.ui.components.ext.LayeredPaneComponent;

public class JSLayeredPane extends JLayeredPane implements IHasComponent<LayeredPaneComponent>, IHasDrawer 
{
    private IDrawer  drawer;
    private LayeredPaneComponent comp;    
    
    JSLayeredPane ()
    {
    }    

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
    public final LayeredPaneComponent getComponent()
    {
        return comp;
    }

    @Override
    public final void setComponent(LayeredPaneComponent scomp)
    {
        this.comp = scomp;
    }
}
