package gent.timdemey.cards.services.scaling;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.services.interfaces.IFrameService;
import gent.timdemey.cards.services.panels.IPanelManager;
import gent.timdemey.cards.utils.DebugDrawDefines;

public abstract class ScalableComponent implements IScalableComponent
{
    private final UUID id;
    private final ComponentType compType;
    
    private IPanelManager panelManager;
    private JComponent component = null;
    private boolean mirror = false;
    private Coords.Absolute coords = null;
    private Object payload = null;

    private JMouseAdapter mouseAdapter = null;

    protected ScalableComponent(UUID id, ComponentType compType)
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
    }

    @Override
    public Object getPayload()
    {
        return payload;
    }
    
    @Override
    public void setPayload(Object payload)
    {
        this.payload = payload;
    }

    @Override
    public ComponentType getComponentType()
    {
        return compType;
    }
    
    @Override
    public IPanelManager getPanelManager()
    {
        return panelManager;
    }
    
    @Override
    public void setPanelManager(IPanelManager panelManager)
    {
        this.panelManager = panelManager;
    }

    @Override
    public final JComponent getComponent()
    {
        if(component == null)
        {
            component = new JScalableComponent(this);
        }
        return component;
    }

    protected abstract void draw(Graphics2D g2);

    public final void drawDebug(Graphics2D g2)
    {
        if(!Services.get(IFrameService.class).getDrawDebug())
        {
            return;
        }
        Coords.Absolute coords = getCoords();
        // dimmed overlay color, to make (white) debug text readable
        {
            Graphics2D g = (Graphics2D) g2.create();
            g.setColor(DebugDrawDefines.COLOR_DIMMED_COMPONENT_BACKGROUND);
            g.fillRect(0, 0, coords.w, coords.h);
        }

        drawDebugBoundaries(g2);

        List<String> debugStrings = getDebugStrings();
        drawDebugStrings(g2, debugStrings);
    }

    protected List<String> getDebugStrings()
    {
        int width = getComponent().getWidth();
        int height = getComponent().getHeight();
        int x = getComponent().getX();
        int y = getComponent().getY();

        int layer = panelManager.getLayer(this);

        return Arrays.asList("rect=" + x + "," + y + ", " + width + "x" + height, "layer=" + layer);
    }

    protected void drawDebugBoundaries(Graphics2D g2)
    {
        Rectangle bounds = getCoords().getBounds();
        g2.setColor(DebugDrawDefines.COLOR_SCALABLECOMPONENT_BOUNDINGBOX);
        g2.drawRect(0, 0, bounds.width - 1, bounds.height - 1);
    }

    private final void drawDebugStrings(Graphics2D g2, List<String> strings)
    {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.white);

        if(strings.size() == 0)
        {
            return;
        }

        g2.setFont(Font.decode("Arial bold 8"));
        int hFat = g2.getFontMetrics().getHeight();
        g2.drawString(strings.get(0), 5, hFat);

        g2.setFont(Font.decode("Arial 8"));
        int hSmall = g2.getFontMetrics().getHeight();

        for (int i = 1; i < strings.size(); i++)
        {
            g2.drawString(strings.get(i), 5, hFat + i * hSmall);
        }
    }

    @Override
    public void setCoords(Coords.Absolute coords)
    {
        this.coords = coords;
        getComponent().setBounds(coords.getBounds());
    }

    @Override
    public Coords.Absolute getCoords()
    {
        return coords;
    }

    @Override
    public void setLocation(int x, int y)
    {
        Coords.Absolute coords_new = Coords.getAbsolute(x, y, coords.w, coords.h);
        setCoords(coords_new);
    }

    @Override
    public final void setMirror(boolean mirror)
    {
        this.mirror = mirror;
    }

    public final boolean isMirror()
    {
        return this.mirror;
    }

    public final void repaint()
    {
        getComponent().repaint();
    }

    @Override
    public UUID getId()
    {
        return id;
    }

    @Override
    public final void add(IScalableComponentMouseListener listener)
    {
        if(mouseAdapter == null)
        {
            mouseAdapter = new JMouseAdapter();
            getComponent().addMouseListener(mouseAdapter);
            getComponent().addMouseMotionListener(mouseAdapter);
            getComponent().addMouseWheelListener(mouseAdapter);
        }

        mouseAdapter.add(listener);
    }

    @Override
    public final void remove(IScalableComponentMouseListener listener)
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

}
