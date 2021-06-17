package gent.timdemey.cards.ui.components.ext;

import java.util.UUID;

import javax.swing.JComponent;

import gent.timdemey.cards.services.contract.Coords;
import gent.timdemey.cards.services.contract.descriptors.ComponentType;
import gent.timdemey.cards.ui.panels.IPanelManager;

public class SComponent implements IComponent
{
    private final UUID id;
    private final ComponentType compType;
    
    private IPanelManager panelManager;
    private Coords.Absolute coords = null;
    private JComponent comp;
    private Object payload;

    public SComponent(UUID id, ComponentType compType)
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
    public void onAttached(JComponent comp)
    {
        this.comp = comp;
    }

    @Override
    public JComponent getJComponent()
    {
        return comp;
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
        
    @Override
    public final void setCoords(Coords.Absolute coords)
    {
        this.coords = coords;
        comp.setBounds(coords.getBounds());
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
    public final UUID getId()
    {
        return id;
    }
   
    @Override
    public final Object getPayload()
    {
        return payload;
    }
    
    @Override
    public final void setPayload(Object payload)
    {
        this.payload = payload;
    }

}
