package gent.timdemey.cards.ui.components.swing;

import java.awt.Graphics;

import javax.swing.JLabel;

import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import gent.timdemey.cards.ui.components.ext.TextComponent;

public class JSLabel extends JLabel implements IHasComponent<TextComponent>, IHasDrawer<JLabel> 
{
    private IDrawer<JLabel>  drawer;
    private TextComponent scomp;    
    
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
    public final TextComponent getComponent()
    {
        return scomp;
    }

    @Override
    public final void setComponent(TextComponent scomp)
    {
        this.scomp = scomp;
    }
}
