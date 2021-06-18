package gent.timdemey.cards.ui.components.swing;

import java.awt.Graphics;

import javax.swing.JButton;

import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.ComponentBase;
import gent.timdemey.cards.ui.components.ext.IHasComponent;

public class JSButton extends JButton implements IHasComponent<ComponentBase>, IHasDrawer<JButton> 
{
    private IDrawer<JButton>  drawer;
    private ComponentBase scomp;    
    
    JSButton()
    {
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {        
        drawer.draw(g, super::paintComponent);
    }

    @Override
    public final IDrawer<JButton> getDrawer()
    {
        return drawer;
    }

    @Override
    public void setDrawer(IDrawer<JButton> drawer)
    {
        this.drawer = drawer;
    }

    @Override
    public final ComponentBase getComponent()
    {
        return scomp;
    }

    @Override
    public final void setComponent(ComponentBase scomp)
    {
        this.scomp = scomp;
    }
}
