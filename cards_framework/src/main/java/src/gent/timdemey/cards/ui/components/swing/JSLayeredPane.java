package gent.timdemey.cards.ui.components.swing;

import java.awt.Graphics;

import javax.swing.JLayeredPane;

import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import gent.timdemey.cards.ui.components.ext.IComponent;

public class JSLayeredPane extends JLayeredPane implements IHasComponent, IHasDrawer<JLayeredPane> 
{
    private IDrawer<JLayeredPane>  drawer;
    private IComponent scomp;    
    
    JSLayeredPane ()
    {
    }    

    @Override
    protected void paintComponent(Graphics g)
    {
        drawer.draw(g, super::paintComponent);
    }

    @Override
    public final IDrawer<JLayeredPane> getDrawer()
    {
        return drawer;
    }

    @Override
    public void setDrawer(IDrawer<JLayeredPane> drawer)
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
