package gent.timdemey.cards.ui.components.swing;

import java.awt.Graphics;

import javax.swing.JLabel;

import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.ComponentBase;
import gent.timdemey.cards.ui.components.ext.IHasComponent;

public class JSLabel extends JLabel implements IHasComponent<ComponentBase>, IHasDrawer<JLabel> 
{
    private IDrawer<JLabel>  drawer;
    private ComponentBase comp;    
    
    JSLabel()
    {
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {        
        drawer.draw(g, super::paintComponent);
    }

    @Override
    public final IDrawer<JLabel> getDrawer()
    {
        return drawer;
    }

    @Override
    public void setDrawer(IDrawer<JLabel> drawer)
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
