package gent.timdemey.cards.services.scaleman;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

import javax.swing.JComponent;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.interfaces.IGamePanelService;

public abstract class ScalableComponent implements IScalableComponent
{
    private final UUID id;
    private JComponent component = null;
    private boolean mirror = false;    
    
    protected ScalableComponent(UUID id)
    {
        if (id == null)
        {
            throw new NullPointerException("id cannot be null");
        }
        
        this.id = id;
    }
    
    @Override
    public final JComponent getComponent()
    {
        if (component == null)
        {
            component = createComponent();
        }
        return component;
    }
    
    protected abstract JComponent createComponent();
    
    public void drawDebug(Graphics2D g2)
    {
        if(!Services.get(IGamePanelService.class).getDrawDebug())
        {
            return;
        }
        
        int width = getComponent().getWidth();
        int height = getComponent().getHeight();
        int x = getComponent().getX();
        int y = getComponent().getY();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(50, 50, 50, 100));
        g2.fillRect(0, 0, width, height);

        g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 2.0f,
                new float[] { 5.0f, 5.0f }, 2.0f));
        g2.drawRect(0, 0, width - 1, height - 1);

        IGamePanelService gpServ = Services.get(IGamePanelService.class);
        int layer = gpServ.getLayer(this);
        int zorder = gpServ.getZOrder(this);

        String[] strings = new String[] { "rect=" + x + "," + y + ", " + width + "x" + height, "layer=" + layer,
                "zorder=" + zorder };
        String[] extraStrings = addDebugStrings();
        String[] allStrings;
        if (extraStrings != null && extraStrings.length > 0)
        {
            allStrings = Stream.concat(Arrays.stream(strings), Arrays.stream(extraStrings)).toArray(String[]::new);
        }
        else
        {
            allStrings = strings;
        }

        drawDebugStrings(g2, allStrings);
    }

    protected abstract String[] addDebugStrings();

    private void drawDebugStrings(Graphics2D g2, String[] strings)
    {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.white);

        if (strings.length == 0)
        {
            return;
        }

        g2.setFont(Font.decode("Arial bold 8"));
        int hFat = g2.getFontMetrics().getHeight();
        g2.drawString(strings[0], 5, hFat);

        g2.setFont(Font.decode("Arial 8"));
        int hSmall = g2.getFontMetrics().getHeight();

        for (int i = 1; i < strings.length; i++)
        {
            g2.drawString(strings[i], 5, hFat + i * hSmall);
        }
    }

    @Override
    public final void setBounds(Rectangle rect)
    {
        getComponent().setBounds(rect);
    }
    
    @Override
    public final void setBounds(int x, int y, int w, int h)
    {
        getComponent().setBounds(x,y,w,h);
    }
    
    @Override
    public Rectangle getBounds()
    {
        return getComponent().getBounds();
    }
    

    @Override
    public void setLocation(int x, int y)
    {
        getComponent().setLocation(x, y);
    }
    
    @Override
    public final void setMirror(boolean mirror)
    {
        this.mirror = mirror;
    }
    
    public final boolean isMirror ()
    {
        return this.mirror;
    }

    public final void repaint()
    {
        getComponent().repaint();
    }


    @Override
    public final void setForeground(Color color)
    {
        getComponent().setForeground(color);
    }
    
    @Override
    public UUID getId()
    {
        return id;
    }
}
