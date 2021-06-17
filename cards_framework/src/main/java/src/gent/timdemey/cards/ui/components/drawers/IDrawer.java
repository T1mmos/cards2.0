package gent.timdemey.cards.ui.components.drawers;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import javax.swing.JComponent;

import gent.timdemey.cards.ui.components.IAttachable;

public interface IDrawer<T extends JComponent> extends IAttachable<T>
{    
    void draw(Graphics g, Consumer<Graphics> paintComponent);    
    
    void setMirror(boolean mirror);
    boolean isMirror();
    
    public void setForegroundAlpha(float alpha);    
    public float getForegroundAlpha();
    
    public void setBackgroundAlpha(float alpha);
    public float getBackgroundAlpha();
    
    public void setBackgroundImage(BufferedImage bg);
    public BufferedImage getBackgroundImage();
}
