package gent.timdemey.cards.ui.components.swing;

import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.ComponentBase;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import java.awt.Graphics;
import javax.swing.JButton;

public class JSButton extends JButton implements IHasComponent<ComponentBase>, IHasDrawer
{
    private IDrawer drawer;
    private ComponentBase scomp;    
    
    JSButton()
    {
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
    protected void paintComponent(Graphics g)
    {        
        drawer.draw(g, super::paintComponent);
    }
    
    @Override
    protected void paintChildren(Graphics g)
    {
        drawer.drawChildren(g, super::paintChildren);
    }
}
