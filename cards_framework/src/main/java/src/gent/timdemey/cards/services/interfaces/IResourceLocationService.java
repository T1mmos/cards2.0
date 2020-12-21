package gent.timdemey.cards.services.interfaces;

import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;

public interface IResourceLocationService
{
    public String getAppBackgroundImageFilePath();
    
    public String getCardFrontFilePath(Suit suit, Value value);    
    public String getCardBackFilePath();
    
    public String getAppIconFilePath(int dim);
    public String getAppMinimizeIconFilePath();
    public String getAppMaximizeIconFilePath();
    public String getAppMaximizeUndoIconFilePath();
    public String getAppCloseIconFilePath();
    public String getAppTitleFontFilePath();
    
    public String getMenuFontFilePath();    
}
