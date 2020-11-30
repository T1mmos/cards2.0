package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;

public interface IResourceLocationService
{
    public String getCardFrontFilePath(Suit suit, Value value);    
    public String getCardBackFilePath();
    
    public String getAppIconFilePath(int dim);
    public String getMenuFontFilePath();
}
