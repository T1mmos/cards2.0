package gent.timdemey.cards.ui.components.swing;

import java.awt.Graphics;

import javax.swing.JButton;

import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.ext.IHasComponent;

public class JSButton extends JButton implements IHasComponent, IHasDrawer<JButton> 
{
    private IDrawer<JButton>  drawer;
    private IComponent scomp;    
    
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
