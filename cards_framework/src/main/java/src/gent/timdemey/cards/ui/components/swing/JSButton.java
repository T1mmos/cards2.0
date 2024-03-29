package gent.timdemey.cards.ui.components.swing;

import java.awt.Graphics;

import com.alee.laf.button.WebButton;

import gent.timdemey.cards.ui.components.drawers.ButtonDrawer;
import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.ComponentBase;
import gent.timdemey.cards.ui.components.ext.IHasComponent;

public class JSButton extends WebButton implements IHasComponent<ComponentBase>, IHasDrawer 
{
    private ButtonDrawer drawer;
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
    protected void paintChildren(Graphics g)
    {
        drawer.drawChildren(g, super::paintChildren);
    }

    @Override
    public final ButtonDrawer getDrawer()
    {
        return drawer;
    }

    @Override
    public void setDrawer(IDrawer drawer)
    {
        this.drawer = (ButtonDrawer) drawer;
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
