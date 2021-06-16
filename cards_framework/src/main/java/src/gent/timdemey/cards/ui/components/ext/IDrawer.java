package gent.timdemey.cards.ui.components.ext;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public interface IDrawer
{
    void draw(Graphics2D g, JComponent comp);
    
    void setMirror(boolean mirror);
    boolean isMirror();
    
    public void setForegroundAlpha(float alpha);    
    public float getForegroundAlpha();
    
    public void setBackgroundAlpha(float alpha);
    public float getBackgroundAlpha();
    
    public BufferedImage getTile();    
}
