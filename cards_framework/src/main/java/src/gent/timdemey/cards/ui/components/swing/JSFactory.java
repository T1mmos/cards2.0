package gent.timdemey.cards.ui.components.swing;

import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.ui.components.SFontResource;
import gent.timdemey.cards.ui.components.SImageResource;
import gent.timdemey.cards.ui.components.drawers.DrawerBase;
import gent.timdemey.cards.ui.components.drawers.IDrawer;
import gent.timdemey.cards.ui.components.drawers.IHasDrawer;
import gent.timdemey.cards.ui.components.drawers.ScaledImageDrawer;
import gent.timdemey.cards.ui.components.drawers.ScaledTextDrawer;
import gent.timdemey.cards.ui.components.ext.ComponentBase;
import gent.timdemey.cards.ui.components.ext.IComponent;
import gent.timdemey.cards.ui.components.ext.IHasComponent;
import gent.timdemey.cards.ui.components.ext.LayeredPaneComponent;
import net.miginfocom.swing.MigLayout;

public final class JSFactory
{
    private JSFactory() {}
    

    public static JSButton createButton(ActionBase action)
    {
        JSButton jsbutton = new JSButton();

        setComponent(jsbutton, new ComponentBase(UUID.randomUUID(), ComponentTypes.BUTTON));
        setDrawer(jsbutton, new DrawerBase<>());

        return jsbutton;
    }
    
    public static JSButton createButton(String text)
    {
        JSButton jsbutton = new JSButton();
        
        setComponent(jsbutton, new ComponentBase(UUID.randomUUID(), ComponentTypes.BUTTON));
        setDrawer(jsbutton, new DrawerBase<>());
        
        jsbutton.setText(text);
        
        return jsbutton;
    }

    
    public static JSLabel createLabel(String text)
    {        
        return createLabel(text, ComponentTypes.LABEL);
    }
    
    public static JSLabel createLabel(String text, ComponentType compType)
    {        
        return createLabel(text, compType, new DrawerBase<>());
    }
    
    public static JSLabel createLabelScaled(String text, ComponentType compType, SFontResource fontRes)
    {
        return createLabel(text, compType, new ScaledTextDrawer(fontRes));    
    }
    
    public static JSLabel createLabel(String text, ComponentType compType, IDrawer<JLabel> drawer)
    {
        JSLabel jslabel = new JSLabel();

        jslabel.setText(text);
        
        setComponent(jslabel, new ComponentBase(UUID.randomUUID(), compType));
        setDrawer(jslabel, drawer);
        
        return jslabel;
    }
    
    public static JSLayeredPane createLayeredPane(ComponentType compType)
    {        
        return createLayeredPane(compType, new DrawerBase<>());
    }

    public static JSLayeredPane createLayeredPane(ComponentType compType, IDrawer<JLayeredPane> drawer)
    {
        JSLayeredPane jslpane = new JSLayeredPane();
        
        // default layout manager
        jslpane.setLayout(new MigLayout("insets 0"));
        
        setComponent(jslpane, new LayeredPaneComponent(UUID.randomUUID(), compType));
        setDrawer(jslpane, drawer);
        
        return jslpane;
    }
    
    public static JSImage createImage(ComponentType compType)
    {
        return createImage(UUID.randomUUID(), compType);
    }
    
    public static JSImage createImage(UUID compId, ComponentType compType)
    {
        return createImage(compId, compType, new DrawerBase<>());
    }
    
    public static JSImage createImageScaled(UUID compId, ComponentType compType, SImageResource... resources)
    {   
        ScaledImageDrawer drawer = new ScaledImageDrawer(resources);
        
        if (resources.length > 0)
        {
            drawer.setScalableImageResource(resources[0].id);    
        }
        
        return createImage(compId, compType, drawer);
    }
    
    public static JSImage createImage(UUID compId, ComponentType compType, IDrawer<JPanel> drawer)
    {
        JSImage jsimage = new JSImage();
        setComponent(jsimage, new ComponentBase(compId, compType));
        setDrawer(jsimage, drawer);
        return jsimage;
    }
        
    private static <T extends JComponent> void setDrawer(IHasDrawer<T> drawee, IDrawer<T> drawer)
    {
        drawee.setDrawer(drawer);
        drawer.onAttached((T) drawee);
    }

    private static <T extends IComponent, J extends JComponent & IHasComponent<T>> void setComponent(J hasComp, T comp)
    {
        hasComp.setComponent(comp);
        comp.onAttached(hasComp);
    }
}
