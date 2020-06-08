package gent.timdemey.cards.services.interfaces;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JMenuBar;

import gent.timdemey.cards.ICardPlugin;

public interface IFrameService
{
    public List<Image> getFrameIcons();
    
    public JMenuBar getMenuBar(ICardPlugin plugin);

    public BufferedImage getBackground();
}
