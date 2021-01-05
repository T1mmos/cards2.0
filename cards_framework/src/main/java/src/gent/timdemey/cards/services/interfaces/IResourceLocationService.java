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
    public String getAppMinimizeRolloverIconFilePath();
    public String getAppMaximizeIconFilePath();
    public String getAppMaximizeRolloverIconFilePath();
    public String getAppUnmaximizeIconFilePath();
    public String getAppUnmaximizeRolloverIconFilePath();
    public String getAppCloseIconFilePath();
    public String getAppCloseRolloverIconFilePath();
    public String getAppTitleFontFilePath();
    
    public String getMenuFontFilePath();    
}
