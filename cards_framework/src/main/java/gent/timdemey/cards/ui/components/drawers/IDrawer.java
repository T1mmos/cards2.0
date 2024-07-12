package gent.timdemey.cards.ui.components.drawers;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import gent.timdemey.cards.ui.components.IAttachable;

public interface IDrawer extends IAttachable
{    
    void draw(Graphics g, Consumer<Graphics> paintComponent);    
    void drawChildren(Graphics g, Consumer<Graphics> paintChildren);    
    
    void setMirror(boolean mirror);
    boolean isMirror();
    
    public void setForegroundAlpha(float alpha);    
    public float getForegroundAlpha();
    
    public void setBackgroundAlpha(float alpha);
    public float getBackgroundAlpha();
    
    public void setBackgroundImage(BufferedImage bg);
    public BufferedImage getBackgroundImage();
}
