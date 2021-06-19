package gent.timdemey.cards.utils;

import javax.swing.JComponent;

import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.ext.IHasComponent;

public class ComponentUtils
{
    private ComponentUtils () {}       
    
    public static IComponent getComponent(JComponent comp)
    {
        if (!(comp instanceof IHasComponent))
        {
            throw new IllegalStateException("Expected the component to implement IHasComponent");
        }
        
        IComponent sComp = ((IHasComponent<?>) comp).getComponent();
        return sComp;
    }
        
    public static IDrawer<?> getDrawer(JComponent comp)
    {
        if (!(comp instanceof IHasDrawer<?>))
        {
            throw new IllegalStateException("Expected the component to implement IHasDrawer");
        }
        
        IDrawer<?> drawer = ((IHasDrawer<?>) comp).getDrawer();
        return drawer;
    }
    
    public static ComponentType getComponentType(JComponent jcomp)
    {
        IComponent comp = getComponent(jcomp);
        ComponentType compType = comp.getComponentType();
        return compType;
    }
}
