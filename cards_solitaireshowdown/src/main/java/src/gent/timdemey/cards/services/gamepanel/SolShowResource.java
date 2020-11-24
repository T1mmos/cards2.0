package gent.timdemey.cards.services.gamepanel;

import java.awt.Color;

import gent.timdemey.cards.utils.ColorUtils;

public class SolShowResource
{
    static final String FILEPATH_IMG_CARDSTACK = "stack_solitaire_%s.png";

    static final String FILEPATH_FONT_SCORE = "SMB2.ttf";
    static final String FILEPATH_FONT_SPECIALCOUNT = "ChangaOne-Regular.ttf";
    static final String FILEPATH_FONT_PLAYERNAME = "ChangaOne-Regular.ttf";

    static final String FILEPATH_IMG_CARDSTACK_TURNOVER = "stack_solshow_turnover.png";
    static final String FILEPATH_IMG_CARDSTACK_SPECIAL = "stack_solshow_special.png";
    static final String FILEPATH_IMG_BACKGROUND_SPECIAL_REMOTE = "special_background_red.png";
    static final String FILEPATH_IMG_BACKGROUND_SPECIAL_LOCAL = "special_background_yellow.png";
    static final String FILEPATH_IMG_BACKGROUND_CARDAREA_REMOTE = "cardbackground_red.png"; 
    static final String FILEPATH_IMG_BACKGROUND_CARDAREA_LOCAL = "cardbackground_green.png";
    static final String FILEPATH_IMG_BACKGROUND_PLAYER_REMOTE = "playerbackground_red.png";
    static final String FILEPATH_IMG_BACKGROUND_PLAYER_LOCAL = "playerbackground_green.png";

    static final Color COLOR_ANIMATION_CARDSCORE_INNER_START = ColorUtils.rgba("#FF7644FF");
    static final Color COLOR_ANIMATION_CARDSCORE_INNER_END = ColorUtils.rgba("#FF764488");
    static final Color COLOR_ANIMATION_CARDSCORE_OUTER_START =  ColorUtils.rgba("#FF0000FF");
    static final Color COLOR_ANIMATION_CARDSCORE_OUTER_END = ColorUtils.rgba("#FFFFFF88");
    static final Color COLOR_FONT_SPECIALCOUNT_INNER = ColorUtils.rgb("#CCE1F2");
    static final Color COLOR_FONT_SPECIALCOUNT_OUTER = ColorUtils.rgb("#444444");
    static final Color COLOR_FONT_PLAYERNAME_INNER = ColorUtils.rgb("#E0BB00");
    static final Color COLOR_FONT_PLAYERNAME_OUTER = ColorUtils.rgb("#444444");

    static final int TIME_MS_ANIMATION_CARDSCORE = 1500;
   
    
    private SolShowResource()
    {

    }
}

