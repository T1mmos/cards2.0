package gent.timdemey.cards.services;

import java.awt.Font;
import java.awt.image.BufferedImage;

public interface IResourceService
{
    BufferedImage getImage(String filename);
    Font getFont(String filename);
    
    void clear();
}
