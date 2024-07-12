package gent.timdemey.cards.ui.components.swing;

import java.awt.Graphics;

import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.ComponentBase;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import javax.swing.JButton;

public class JSButton extends JButton implements IHasComponent<ComponentBase>
{
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
}
