package gent.timdemey.cards.services.resources;

import gent.timdemey.cards.model.entities.cards.Suit;
import gent.timdemey.cards.model.entities.cards.Value;
import gent.timdemey.cards.services.interfaces.IResourceLocationService;

public class ResourceLocationService implements IResourceLocationService
{
    private static final String APP_ICON = "icon_spade_%sx%s.png";    
    private static final String FILEPATH_FONT_MENU = "ARCADE.TTF";

    private static final String FILEPATH_CARD_FRONTSIDE = "cards/edge_thick/%s_%s.png";
    private static final String FILEPATH_CARD_BACKSIDE = "cards/edge_thick/backside_yellow.png";
    
    @Override
    public String getCardFrontFilePath(Suit suit, Value value)
    {
        String suit_str = suit.name().substring(0, 1);
        String value_str = value.getTextual();

        return String.format(FILEPATH_CARD_FRONTSIDE, suit_str, value_str);
    }

    @Override
    public String getCardBackFilePath()
    {
        return FILEPATH_CARD_BACKSIDE;
    }

    @Override
    public String getAppIconFilePath(int dim)
    {
        String dimstr = Integer.toString(dim);
        return String.format(APP_ICON, dimstr, dimstr);
    }

    @Override
    public String getMenuFontFilePath()
    {
        return FILEPATH_FONT_MENU;
    }

    @Override
    public String getAppMinimizeIconFilePath()
    {
        return "minimize.png";
    }

    @Override
    public String getAppMaximizeIconFilePath()
    {
        return "maximize.png";
    }
    
    @Override
    public String getAppMaximizeUndoIconFilePath()
    {
        return "maximize_undo.png";
    }
    
    @Override
    public String getAppTitleFontFilePath()
    {
        return "foptitles.ttf";
    }

    @Override
    public String getAppCloseIconFilePath()
    {
        return "close.png";
    }

    @Override
    public String getAppBackgroundImageFilePath()
    {
        return "background_darkgray.png";
    }

    
}
