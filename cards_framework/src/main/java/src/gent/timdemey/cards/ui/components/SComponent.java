package gent.timdemey.cards.ui.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.ui.components.ext.IHasDrawer;
import gent.timdemey.cards.ui.components.ext.IHasMouse;
import gent.timdemey.cards.ui.components.ext.IHasPayload;
import gent.timdemey.cards.ui.panels.IPanelManager;
import gent.timdemey.cards.utils.ColorUtils;
import gent.timdemey.cards.utils.DebugDrawDefines;

public abstract class SComponent implements IHasDrawer, IHasPayload
{
    private final UUID id;
    private final ComponentType compType;
    private final SComponent parent;
    
    private IPanelManager panelManager;
    private JComponent component = null;
    
    private Coords.Absolute coords = null;

    private JMouseAdapter mouseAdapter = null;
    
    private boolean visible = true;

    protected SComponent(UUID id, ComponentType compType)
    {
        this(id, compType, null);
    }

    protected SComponent(UUID id, ComponentType compType, SComponent parent)
    {
        if(id == null)
        {
            throw new NullPointerException("id cannot be null");
        }
        if(compType == null)
        {
            throw new NullPointerException("compType cannot be null");
        }

        this.id = id;
        this.compType = compType;
        this.parent = parent;
    }
    

    @Override
    public final ComponentType getComponentType()
    {
        return compType;
    }
    
    @Override
    public final IPanelManager getPanelManager()
    {
        return panelManager;
    }
    
    @Override
    public final void setPanelManager(IPanelManager panelManager)
    {
        this.panelManager = panelManager;
    }

    protected JComponent getJComponent()
    {
        if(component == null)
        {
            component = createJComponent();
        }
        
        return component;
    }
    
    protected abstract JComponent createJComponent();
    
    public final SComponent getParent()
    {
        return parent;
    }
  
    @Override
    public final void setCoords(Coords.Absolute coords)
    {
        this.coords = coords;
        getJComponent().setBounds(coords.getBounds());
    }

    @Override
    public final Coords.Absolute getCoords()
    {
        return coords;
    }

    @Override
    public final void setLocation(int x, int y)
    {
        Coords.Absolute coords_new = Coords.getAbsolute(x, y, coords.w, coords.h);
        setCoords(coords_new);
    }
    
    @Override
    public UUID getId()
    {
        return id;
    }

    @Override
    public final void add(IHasMouse listener)
    {
        if(mouseAdapter == null)
        {
            mouseAdapter = new JMouseAdapter();
            getJComponent().addMouseListener(mouseAdapter);
            getJComponent().addMouseMotionListener(mouseAdapter);
            getJComponent().addMouseWheelListener(mouseAdapter);
        }

        mouseAdapter.add(listener);
    }

    @Override
    public final void remove(IHasMouse listener)
    {
        if(mouseAdapter == null)
        {
            return;
        }

        mouseAdapter.remove(listener);
        if(!mouseAdapter.hasListeners())
        {
            mouseAdapter = null;
        }
    }
    
    
    final void applyAlpha(Graphics2D g, float alpha)
    {
        Composite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
        g.setComposite(composite);
    }    
}
