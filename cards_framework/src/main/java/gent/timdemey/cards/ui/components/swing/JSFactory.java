package gent.timdemey.cards.ui.components.swing;

import gent.timdemey.cards.Services;
import java.util.UUID;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.interfaces.IResourceCacheService;
import gent.timdemey.cards.services.interfaces.IResourceNameService;
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
import java.awt.Color;
import net.miginfocom.swing.MigLayout;

public final class JSFactory
{
    private JSFactory() {}
    

    public static JSButton createButton(ActionBase action)
    {
        JSButton jsbutton = createButton();

        jsbutton.setAction(action);
     
        return jsbutton;
    }
    
    public static JSButton createButton(String text)
    {
        JSButton jsbutton = createButton();
        
        jsbutton.setText(text);       
        
        return jsbutton;
    }
    
    private static JSButton createButton()
    {
        JSButton jsbutton = new JSButton();
        
        setComponent(jsbutton, new ComponentBase(UUID.randomUUID(), ComponentTypes.BUTTON));
        setFont(jsbutton);    
        setBackgroundTransparent(jsbutton);    
        setDrawer(jsbutton, new DrawerBase<JSButton>()); 
        
        return jsbutton;
    }

    
    public static JSLabel createLabel(String text)
    {        
        return createLabel(UUID.randomUUID(), text, ComponentTypes.LABEL);
    }
    
    public static JSLabel createLabel(UUID uuid, String text, ComponentType compType)
    {        
        return createLabel(uuid, text, compType, new DrawerBase<JSLabel>());
    }
    
    public static JSLabel createLabelScaled(UUID uuid, String text, ComponentType compType, SFontResource fontRes)
    {
        JSLabel lbl = createLabel(uuid, text, compType, new ScaledTextDrawer(fontRes));        
        return lbl;
    }
    
    public static JSLabel createLabel(UUID uuid, String text, ComponentType compType, IDrawer drawer)
    {
        JSLabel jslabel = new JSLabel();

        jslabel.setText(text);
        jslabel.setHorizontalTextPosition(JSLabel.CENTER);
        
        setComponent(jslabel, new ComponentBase(uuid, compType));
        setFont(jslabel);
        setDrawer(jslabel, drawer);
        setBackgroundTransparent(jslabel);
        
        return jslabel;
    }
    
    public static JSLabel createLabel(ImageIcon icon)
    {
        return createLabel(icon, ComponentTypes.LABEL);
    }
    
    public static JSLabel createLabel(ImageIcon icon, ComponentType compType)
    {        
        return createLabel(icon, compType, new DrawerBase<JSLabel>());
    }
    
    public static JSLabel createLabel(ImageIcon icon, ComponentType compType, IDrawer drawer)
    {
        JSLabel jslabel = new JSLabel();

        jslabel.setIcon(icon);
        
        setComponent(jslabel, new ComponentBase(UUID.randomUUID(), compType));
        setDrawer(jslabel, drawer);
        setBackgroundTransparent(jslabel);
        
        return jslabel;
    }
    
    public static JSLayeredPane createLayeredPane(ComponentType compType)
    {        
        return createLayeredPane(compType, new DrawerBase<JSLayeredPane>());
    }

    public static JSLayeredPane createLayeredPane(ComponentType compType, IDrawer drawer)
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
        return createImage(compId, compType, new DrawerBase<JSImage>());
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
    
    public static JSImage createImage(UUID compId, ComponentType compType, IDrawer drawer)
    {
        JSImage jsimage = new JSImage();
        setComponent(jsimage, new ComponentBase(compId, compType));
        setDrawer(jsimage, drawer);
        
        setBackgroundTransparent(jsimage);
        
        return jsimage;
    }
        
    private static void setBackgroundTransparent(JComponent jcomp)
    {
        jcomp.setBackground(new Color(255,0,0,0));
    }
    
    private static <S extends JComponent & IHasDrawer> void setDrawer(S drawee, IDrawer drawer)
    {
        drawee.setDrawer(drawer);
        drawer.onAttached(drawee);        
    }

    private static <T extends IComponent, J extends JComponent & IHasComponent<T>> void setComponent(J hasComp, T comp)
    {
        hasComp.setComponent(comp);
        comp.onAttached(hasComp);
    }

    private static void setFont(JComponent comp)
    {
        IResourceCacheService resServ = Services.get(IResourceCacheService.class);
        IResourceNameService resLocServ = Services.get(IResourceNameService.class);
        
        String dialogLabelFont = resLocServ.getFilePath(ResourceDescriptors.DialogLabelFont);
        FontResource res_dialogLabelFont = resServ.getFont(dialogLabelFont);
        comp.setFont(res_dialogLabelFont.raw.deriveFont(40f));
    }
}
