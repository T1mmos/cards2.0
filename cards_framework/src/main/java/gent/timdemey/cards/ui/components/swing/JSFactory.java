package gent.timdemey.cards.ui.components.swing;


import gent.timdemey.cards.di.Container;
import gent.timdemey.cards.logging.Logger;
import java.util.UUID;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import gent.timdemey.cards.services.action.ActionBase;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.contract.descriptors.ComponentTypes;
import gent.timdemey.cards.services.contract.descriptors.ResourceDescriptors;
import gent.timdemey.cards.services.contract.res.FontResource;
import gent.timdemey.cards.services.interfaces.IFrameService;
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
    private final Container _Container;
    private final IResourceNameService _ResourceNameService;
    private final IResourceCacheService _ResourceCacheService;
    
    public JSFactory(
       Container container,
       IResourceNameService resourceNameService,
       IResourceCacheService resourceCacheService) 
    {
        this._Container = container;
        this._ResourceNameService = resourceNameService;
        this._ResourceCacheService = resourceCacheService;
    }
    

    public JSButton createButton(ActionBase action)
    {
        JSButton jsbutton = createButton();

        jsbutton.setAction(action);
     
        return jsbutton;
    }
    
    public JSButton createButton(String text)
    {
        JSButton jsbutton = createButton();
        
        jsbutton.setText(text);       
        
        return jsbutton;
    }
    
    private JSButton createButton()
    {
        JSButton jsbutton = new JSButton();
        
        setComponent(jsbutton, new ComponentBase(UUID.randomUUID(), ComponentTypes.BUTTON));
        setFont(jsbutton);    
        setBackgroundTransparent(jsbutton);    
        setDrawer(jsbutton, _Container.Get(DrawerBase.class)); // was generic: <JSButton>>
        
        return jsbutton;
    }

    
    public JSLabel createLabel(String text)
    {        
        return createLabel(UUID.randomUUID(), text, ComponentTypes.LABEL);
    }
    
    public JSLabel createLabel(UUID uuid, String text, ComponentType compType)
    {        
        return createLabel(uuid, text, compType, _Container.Get(DrawerBase.class)); // was generic: <JSButton>>
    }
    
    public JSLabel createLabelScaled(UUID uuid, String text, ComponentType compType, SFontResource fontRes)
    {
        JSLabel lbl = createLabel(uuid, text, compType, _Container.Get(ScaledTextDrawer.class));        
        return lbl;
    }
    
    public JSLabel createLabel(UUID uuid, String text, ComponentType compType, IDrawer drawer)
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
    
    public JSLabel createLabel(ImageIcon icon)
    {
        return createLabel(icon, ComponentTypes.LABEL);
    }
    
    public JSLabel createLabel(ImageIcon icon, ComponentType compType)
    {        
        return createLabel(icon, compType, _Container.Get(DrawerBase.class)); // was generic: <JSLabel>
    }
    
    public JSLabel createLabel(ImageIcon icon, ComponentType compType, IDrawer drawer)
    {
        JSLabel jslabel = new JSLabel();

        jslabel.setIcon(icon);
        
        setComponent(jslabel, new ComponentBase(UUID.randomUUID(), compType));
        setDrawer(jslabel, drawer);
        setBackgroundTransparent(jslabel);
        
        return jslabel;
    }
    
    public JSLayeredPane createLayeredPane(ComponentType compType)
    {        
        return createLayeredPane(compType, _Container.Get(DrawerBase.class)); // was generic: <JSLayeredPane>
    }

    public JSLayeredPane createLayeredPane(ComponentType compType, IDrawer drawer)
    {
        JSLayeredPane jslpane = new JSLayeredPane();
        
        // default layout manager
        jslpane.setLayout(new MigLayout("insets 0"));
        
        setComponent(jslpane, new LayeredPaneComponent(UUID.randomUUID(), compType));
        setDrawer(jslpane, drawer);
        
        return jslpane;
    }
    
    public JSImage createImage(ComponentType compType)
    {
        return createImage(UUID.randomUUID(), compType);
    }
    
    public JSImage createImage(UUID compId, ComponentType compType)
    {
        return createImage(compId, compType, _Container.Get(DrawerBase.class)); // was generic: <JSImage>
    }
    
    public JSImage createImageScaled(UUID compId, ComponentType compType, SImageResource... resources)
    {   
        ScaledImageDrawer drawer = new ScaledImageDrawer(
                _Container.Get(IFrameService.class), 
                _Container.Get(Logger.class), 
                resources);
        
        if (resources.length > 0)
        {
            drawer.setScalableImageResource(resources[0].id);    
        }
        
        return createImage(compId, compType, drawer);
    }
    
    public JSImage createImage(UUID compId, ComponentType compType, IDrawer drawer)
    {
        JSImage jsimage = new JSImage();
        setComponent(jsimage, new ComponentBase(compId, compType));
        setDrawer(jsimage, drawer);
        
        setBackgroundTransparent(jsimage);
        
        return jsimage;
    }
        
    private void setBackgroundTransparent(JComponent jcomp)
    {
        jcomp.setBackground(new Color(255,0,0,0));
    }
    
    private <S extends JComponent & IHasDrawer> void setDrawer(S drawee, IDrawer drawer)
    {
        drawee.setDrawer(drawer);
        drawer.onAttached(drawee);        
    }

    private <T extends IComponent, J extends JComponent & IHasComponent<T>> void setComponent(J hasComp, T comp)
    {
        hasComp.setComponent(comp);
        comp.onAttached(hasComp);
    }

    private void setFont(JComponent comp)
    {        
        String dialogLabelFont = _ResourceNameService.getFilePath(ResourceDescriptors.DialogLabelFont);
        FontResource res_dialogLabelFont = _ResourceCacheService.getFont(dialogLabelFont);
        comp.setFont(res_dialogLabelFont.raw.deriveFont(40f));
    }
}
