package gent.timdemey.cards.ui.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Point;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;

import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.PanelDescriptor;
import gent.timdemey.cards.ui.components.ext.IDrawer;
import gent.timdemey.cards.ui.components.ext.IHasDrawer;

public class JSLayeredPane extends JLayeredPane implements IHasDrawer, ISComponent
{
    private JLayeredPane pane;
    private IDrawer drawer;
        
    public JSLayeredPane (PanelDescriptor desc, IDrawer drawer)
    {
        this.drawer = drawer;
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        drawer.draw(g, this);
        // super.paintComponents(g);
        
    }

    @Override
    public IDrawer getDrawer()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
