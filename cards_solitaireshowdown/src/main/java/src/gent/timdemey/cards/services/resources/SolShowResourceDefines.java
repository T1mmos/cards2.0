package gent.timdemey.cards.services.resources;

import java.awt.Color;

import gent.timdemey.cards.utils.ColorUtils;

public class SolShowResourceDefines
{
    public static final String FILEPATH_IMG_CARDSTACK = "stack_solitaire_%s.png";

    public static final String FILEPATH_FONT_SCORE = "SMB2.ttf";
    public static final String FILEPATH_FONT_SPECIALCOUNT = "ChangaOne-Regular.ttf";
    public static final String FILEPATH_FONT_PLAYERNAME = "ChangaOne-Regular.ttf";

    public static final String FILEPATH_IMG_CARDSTACK_TURNOVER = "stack_solshow_turnover.png";
    public static final String FILEPATH_IMG_CARDSTACK_SPECIAL = "stack_solshow_special.png";
    public static final String FILEPATH_IMG_BACKGROUND_SPECIAL_REMOTE = "special_background_red.png";
    public static final String FILEPATH_IMG_BACKGROUND_SPECIAL_LOCAL = "special_background_yellow.png";
    public static final String FILEPATH_IMG_BACKGROUND_CARDAREA_REMOTE = "cardbackground_red.png"; 
    public static final String FILEPATH_IMG_BACKGROUND_CARDAREA_LOCAL = "cardbackground_green.png";
    public static final String FILEPATH_IMG_BACKGROUND_PLAYER_REMOTE = "playerbackground_red.png";
    public static final String FILEPATH_IMG_BACKGROUND_PLAYER_LOCAL = "playerbackground_green.png";
    public static final String FILEPATH_IMG_BACKGROUND_VS = "vs.png";

    public static final Color COLOR_ANIMATION_CARDSCORE_INNER_START = ColorUtils.rgb("#FF7644");
    public static final Color COLOR_ANIMATION_CARDSCORE_INNER_END = ColorUtils.rgb("#FF7644");
    public static final Color COLOR_ANIMATION_CARDSCORE_OUTER_START =  ColorUtils.rgb("#000000");
    public static final Color COLOR_ANIMATION_CARDSCORE_OUTER_END = ColorUtils.rgb("#BBBBBB");
    public static final Color COLOR_FONT_SPECIALCOUNT_INNER = ColorUtils.rgb("#CCE1F2");
    public static final Color COLOR_FONT_SPECIALCOUNT_OUTER = ColorUtils.rgb("#444444");
    public static final Color COLOR_FONT_PLAYERNAME_INNER = ColorUtils.rgb("#E0BB00");
    public static final Color COLOR_FONT_PLAYERNAME_OUTER = ColorUtils.rgb("#444444");
    
    private SolShowResourceDefines()
    {

    }
}

