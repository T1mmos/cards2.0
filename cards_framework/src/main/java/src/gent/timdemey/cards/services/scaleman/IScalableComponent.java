package gent.timdemey.cards.services.scaleman;

import java.awt.Rectangle;

import javax.swing.JComponent;

public interface IScalableComponent
{
    public void setMirror(boolean mirror);
    public boolean getMirror();
        
    public void setBounds(Rectangle rect);
    public void setBounds(int x, int y, int w, int h);
    public void setLocation(int x, int y);
    
    public JComponent getComponent();
    public Rectangle getBounds();
}
