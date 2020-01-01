package gent.timdemey.cards.services;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;

public interface IPositionManager {

    public void calculate(int maxWidth, int maxHeight);
    
    public Rectangle getCardSize();
    public Rectangle getCardStackSize(String cardStackType);    
    
    public Rectangle getBounds();    
    public Rectangle getBounds(ReadOnlyCard card);    
    public Rectangle getBounds(ReadOnlyCardStack cardStack);

    public ReadOnlyCardStack getCardStackAt(Point p);
    public List<ReadOnlyCardStack> getCardStacksIn(Rectangle rect);
}
