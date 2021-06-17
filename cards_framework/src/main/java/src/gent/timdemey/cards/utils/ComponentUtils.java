package gent.timdemey.cards.utils;

import javax.swing.JComponent;
import javax.swing.JLabel;

import gent.timdemey.cards.services.contract.descriptors.ComponentType;
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
        
        IComponent sComp = ((IHasComponent) comp).getComponent();
        return sComp;
    }
        
    public static ComponentType getComponentType(JLabel comp)
    {
        ComponentType compType = getComponent(comp).getComponentType();
        return compType;
    }
}
