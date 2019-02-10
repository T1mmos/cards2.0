package gent.timdemey.cards.services.plugin;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import gent.timdemey.cards.entities.E_Card;
import gent.timdemey.cards.entities.E_CardStack;

public interface IPositionManager {

    public void calculate(int maxWidth, int maxHeight);
    
    public Rectangle getCardSize();
    public Rectangle getCardStackSize(String cardStackType);    
    
    public Rectangle getBounds();    
    public Rectangle getBounds(E_Card card);    
    public Rectangle getBounds(E_CardStack cardStack);

    public E_CardStack getCardStackAt(Point p);
    public List<E_CardStack> getCardStacksIn(Rectangle rect);
}
