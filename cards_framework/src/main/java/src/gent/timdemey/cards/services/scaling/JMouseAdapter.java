package gent.timdemey.cards.services.scaling;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.event.MouseInputListener;

class JMouseAdapter implements MouseInputListener, MouseWheelListener
{
    private final List<IScalableComponentMouseListener> listeners;
    
    JMouseAdapter()
    {
        this.listeners = new ArrayList<>();
    }
    
    void add(IScalableComponentMouseListener listener)
    {
        this.listeners.add(listener);
    }
    
    void remove(IScalableComponentMouseListener listener)
    {
        this.listeners.remove(listener);
    }

    public boolean hasListeners()
    {
        return listeners != null && listeners.size() > 0;
    }
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        
        foreach(l -> l.onMouseClicked());
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        foreach(l -> l.onMousePressed());
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        foreach(l -> l.onMouseReleased());
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        foreach(l -> l.onMouseEntered());
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        foreach(l -> l.onMouseExited());
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        foreach(l -> l.onMouseDragged());
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        foreach(l -> l.onMouseMoved());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        foreach(l -> l.onMouseWheelMoved());
    }
    
    private void foreach (Consumer<IScalableComponentMouseListener> action)
    {
        for (IScalableComponentMouseListener listener : listeners)
        {
            action.accept(listener);
        }
    }
}
