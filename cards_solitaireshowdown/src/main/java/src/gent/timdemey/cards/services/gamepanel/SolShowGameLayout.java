package gent.timdemey.cards.services.gamepanel;

final class SolShowGameLayout
{    
    private final class Base
    {
        static final int CWIDTH = 16;                           // card width
        static final int CHEIGHT = 22;                          // card height
        static final int SWIDTH = 18;                           // stack width
        static final int SHEIGHT = 24;                          // base stack height
        static final int SHEIGHTMIDDLE = 60;                    // middle stack height

        static final int PHEIGHT = 8;                           // player name height
        
        static final int SCOFFSETX = 1;                         // offset for a card relative to its stack, horizontally
        static final int SCOFFSETY = 1;                         // offset for a card relative to its stack, vertically
        static final int COFFSETX = 4;                          // offset for a card on top of another card, horizontally
        static final int COFFSETY = 3;                          // offset for a card on top of another card, vertically
        static final int SOFFSETX = 0;                          // offset for a cardstack relative to another cardstack, horizontally 
        static final int SOFFSETY = 1;                          // offset for a cardstack relative to another cardstack, vertically
        static final int SPSCOREHEIGHT = 8;                     // special score text height
        
        static final int AREA_LEFTW = 40;
                
        static final int AREAMARGIN_X = 2;                      // inter-region margin, horizontally
        static final int AREAMARGIN_Y = 3;                      // inter-region margin, vertically    
        static final int AREAPADDING_X = 1;
        static final int AREAPADDING_Y = 1;
        
        static final int AREA_VS_Y = 10;
    }
    
    public static final String DIM_CARD = "DIM_CARD";
    public static final String DIM_CARDSTACK = "DIM_CARDSTACK";
    public static final String DIM_CARDSTACK_MIDDLE = "DIM_CARDSTACK_MIDDLE";
    public static final String DIM_CARDSCORE = "DIM_CARDSCORE";
    
    public static final String RECT_AREA_LEFT = "RECT_AREA_LEFT";
    public static final String RECT_AREA_PLAYERREMOTE = "RECT_AREA_PLAYERREMOTE";
    public static final String RECT_AREA_VS = "RECT_AREA_VS";
    public static final String RECT_AREA_PLAYERLOCAL = "RECT_AREA_PLAYERLOCAL";

    public static final String RECT_AREA_RIGHT = "RECT_AREA_RIGHT";
    public static final String RECT_AREA_CARDSLAYDOWN = "RECT_AREA_CARDSLAYDOWN";
    public static final String RECT_AREA_CARDSLOCAL = "RECT_AREA_CARDSLOCAL";
    public static final String RECT_AREA_CARDSREMOTE = "RECT_AREA_CARDSREMOTE";    
    public static final String RECT_SPECIALCOUNTERTEXT = "DIM_SPECIALCOUNTERTEXT";
    public static final String RECT_SPECIALCOUNTERBACKGROUND = "RECT_SPECIALCOUNTERBACKGROUND";
    public static final String RECT_PLAYERNAME_LOCAL = "RECT_PLAYERNAME_LOCAL";
    public static final String RECT_PLAYERNAME_REMOTE = "RECT_PLAYERNAME_REMOTE";
    public static final String RECT_PLAYERBG_LOCAL = "RECT_PLAYERBG_LOCAL"; 
    public static final String RECT_PLAYERBG_REMOTE = "RECT_PLAYERBG_REMOTE";
    public static final String RECT_CARDAREABG_LOCAL = "RECT_CARDAREABG_LOCAL";
    public static final String RECT_CARDAREABG_REMOTE = "RECT_CARDAREABG_REMOTE";

    public static final String RECT_STACK_DEPOT_0 = "RECT_STACK_DEPOT_0";
    public static final String RECT_STACK_SPECIAL_0 = "RECT_STACK_SPECIAL_0";
    public static final String RECT_STACK_TURNOVER_0 = "RECT_STACK_TURNOVER_0";
    public static final String RECT_STACK_MIDDLE_0 = "RECT_STACK_MIDDLE_0";
    public static final String RECT_STACK_MIDDLE_1 = "RECT_STACK_MIDDLE_1";
    public static final String RECT_STACK_MIDDLE_2 = "RECT_STACK_MIDDLE_2";
    public static final String RECT_STACK_MIDDLE_3 = "RECT_STACK_MIDDLE_3";
    public static final String RECT_STACK_LAYDOWN_0 = "RECT_STACK_LAYDOWN_0";
    public static final String RECT_STACK_LAYDOWN_1 = "RECT_STACK_LAYDOWN_1";
    public static final String RECT_STACK_LAYDOWN_2 = "RECT_STACK_LAYDOWN_2";
    public static final String RECT_STACK_LAYDOWN_3 = "RECT_STACK_LAYDOWN_3";

    
    public static final String OFFSET_STACK_TO_CARD = "OFFSET_STACK_TO_CARD";
    public static final String OFFSET_CARD_TO_CARD = "OFFSET_CARD_TO_CARD";
    public static final String OFFSET_STACK_TO_STACK = "OFFSET_STACK_TO_STACK";
    public static final String HEIGHT_SPECIALSCORE = "HEIGHT_SPECIALSCORE";
    public static final String MARGIN_AREA = "MARGIN_AREA";
    public static final String PADDING_AREA = "PADDING_AREA";
    public static final String PADDING_CONTENT = "PADDING_CONTENT"; 

    
    public static Positions positions_base;
    
    static 
    {
        int area_left_x = 0; 
        int area_left_y = 0;
        
        int area_right_x = Base.AREA_LEFTW + Base.AREAMARGIN_X;
        int area_right_y = 0;
        int area_right_w = 8 * Base.SWIDTH + 7 * Base.SOFFSETX;
        
        int dim_personal_w = area_right_w;
        int dim_personal_h = Base.SHEIGHTMIDDLE + 2 * Base.AREAPADDING_Y;
        
        int area_cardsremote_x = area_right_x;
        int area_cardsremote_y = area_right_y;
        int area_cardsremote_w = dim_personal_w;
        int area_cardsremote_h = dim_personal_h;        
        
        int area_cardslaydown_x = area_right_x;
        int area_cardslaydown_y = area_right_y + dim_personal_h;
        int area_cardslaydown_w = area_right_w;
        int area_cardslaydown_h = Base.SHEIGHT + 2 * Base.AREAPADDING_Y;

        int area_cardslocal_x = area_right_x;
        int area_cardslocal_y = area_cardslaydown_y + area_cardslaydown_h;
        int area_cardslocal_w = dim_personal_w;
        int area_cardslocal_h = dim_personal_h;
        
        int total_w = area_right_x + area_right_w;
        int total_h = area_cardsremote_h + area_cardslaydown_h + area_cardslocal_h;

        int area_left_h = total_h;
        int area_right_h = total_h;
        
        int dim_player_w = Base.AREA_LEFTW;
        int dim_player_h = (area_left_h - Base.AREA_VS_Y - 2 * Base.AREAMARGIN_Y) / 2;
        
        int area_playerremote_x = area_left_x;
        int area_playerremote_y = area_left_y;
        int area_playerremote_w = dim_player_w;
        int area_playerremote_h = dim_player_h;      
        
        int area_playernameremote_x = area_playerremote_x + Base.AREAPADDING_X;
        int area_playernameremote_y = area_playerremote_y + Base.AREAPADDING_Y;
        int area_playernameremote_w = area_playerremote_w - 2 * Base.AREAPADDING_X;
        int area_playernameremote_h = Base.PHEIGHT;
        
        int area_vs_x = area_left_x;
        int area_vs_y = area_left_y + area_playerremote_h + Base.AREAMARGIN_Y;
        int area_vs_w = Base.AREA_LEFTW;
        int area_vs_h = Base.AREA_VS_Y;
        
        int area_playerlocal_x = area_left_x;
        int area_playerlocal_y = area_vs_y + area_vs_h + Base.AREAMARGIN_Y;
        int area_playerlocal_w = Base.AREA_LEFTW;
        int area_playerlocal_h = dim_player_h;                

        int area_playernamelocal_x = area_playerlocal_x + Base.AREAPADDING_X;
        int area_playernamelocal_y = area_playerlocal_y + Base.AREAPADDING_Y;
        int area_playernamelocal_w = area_playerlocal_w - 2 * Base.AREAPADDING_X;
        int area_playernamelocal_h = Base.PHEIGHT;

        int rect_specialscore_text_h = (int) (1.25 * Base.SPSCOREHEIGHT);
        int rect_specialscore_text_x = area_right_x + Base.AREAMARGIN_X;
        int rect_specialscore_text_y = area_cardslocal_y + Base.AREAPADDING_Y + (Base.SHEIGHT - rect_specialscore_text_h) / 2;
        int rect_specialscore_text_w = Base.SWIDTH - Base.AREAPADDING_X;
        
        int rect_specialscore_bg_x = area_right_x + Base.AREAPADDING_X;
        int rect_specialscore_bg_y = area_cardslocal_y + Base.AREAPADDING_Y;
        int rect_specialscore_bg_w = 3 * Base.SWIDTH;
        int rect_specialscore_bg_h = Base.SHEIGHT;
        
        int rect_stack_depot_x = area_cardslocal_x + Base.AREAPADDING_X + (int) (0.5 * Base.SWIDTH);
        int rect_stack_depot_y = area_cardslocal_y + Base.AREAPADDING_Y + (int) (1.5 * Base.SHEIGHT);
        int rect_stack_special_x = area_cardslocal_x + Base.AREAPADDING_X + Base.SWIDTH;
        int rect_stack_special_y = area_cardslocal_y + Base.AREAPADDING_Y;
        int rect_stack_turnover_x = rect_stack_depot_x + Base.SWIDTH + Base.SOFFSETX;
        int rect_stack_turnover_y = rect_stack_depot_y;
        int rect_stack_laydown_y = area_cardslaydown_y + Base.AREAPADDING_Y;
        int rect_stack_middle_x = rect_stack_turnover_x + (int) (2 * Base.SWIDTH);
        int rect_stack_middle_y = rect_stack_special_y;
                
        Positions.Builder builder = new Positions.Builder();
        builder
            .dimension(DIM_CARD, Base.CWIDTH, Base.CHEIGHT)
            .dimension(DIM_CARDSTACK, Base.SWIDTH, Base.SHEIGHT)
            .dimension(DIM_CARDSTACK_MIDDLE, Base.SWIDTH, Base.SHEIGHTMIDDLE)
            .dimension(DIM_CARDSCORE, 2 * Base.CWIDTH, Base.CHEIGHT / 2)
            .coordinate(OFFSET_STACK_TO_CARD, Base.SCOFFSETX, Base.SCOFFSETY)
            .coordinate(OFFSET_CARD_TO_CARD, Base.COFFSETX, Base.COFFSETY)
            .coordinate(OFFSET_STACK_TO_STACK, Base.SOFFSETX, Base.SOFFSETY)
            .length(HEIGHT_SPECIALSCORE, Base.SPSCOREHEIGHT)
            .coordinate(MARGIN_AREA, Base.AREAMARGIN_X, Base.AREAMARGIN_Y)
            .coordinate(PADDING_AREA, Base.AREAPADDING_X, Base.AREAPADDING_Y)
            
            .rectangle(RECT_AREA_LEFT, area_left_x, area_left_y, Base.AREA_LEFTW, area_left_h)
            .rectangle(RECT_AREA_PLAYERREMOTE, area_playerremote_x, area_playerremote_y, area_playerremote_w, area_playerremote_h)
            .rectangle(RECT_PLAYERNAME_REMOTE, area_playernameremote_x, area_playernameremote_y, area_playernameremote_w, area_playernameremote_h)
            .rectangle(RECT_AREA_VS, area_vs_x, area_vs_y, area_vs_w, area_vs_h)
            .rectangle(RECT_AREA_PLAYERLOCAL, area_playerlocal_x, area_playerlocal_y, area_playerlocal_w, area_playerlocal_h)
            .rectangle(RECT_PLAYERNAME_LOCAL, area_playernamelocal_x, area_playernamelocal_y, area_playernamelocal_w, area_playernamelocal_h)
            .rectangle(RECT_PLAYERBG_LOCAL, area_playerlocal_x, area_playerlocal_y, area_playerlocal_w, area_playerlocal_h)
            .rectangle(RECT_PLAYERBG_REMOTE, area_playerremote_x, area_playerremote_y, area_playerremote_w, area_playerremote_h)
            .rectangle(RECT_CARDAREABG_LOCAL, area_cardslocal_x, area_cardslocal_y, area_cardslocal_w, area_cardslocal_h)
            .rectangle(RECT_CARDAREABG_REMOTE, area_cardsremote_x, area_cardsremote_y, area_cardsremote_w, area_cardsremote_h)
            
            .rectangle(RECT_AREA_RIGHT, area_right_x, area_right_y, area_right_w, area_right_h)
            .rectangle(RECT_AREA_CARDSREMOTE, area_cardsremote_x, area_cardsremote_y, area_cardsremote_w, area_cardsremote_h)
            .rectangle(RECT_AREA_CARDSLAYDOWN, area_cardslaydown_x, area_cardslaydown_y, area_cardslaydown_w, area_cardslaydown_h)
            .rectangle(RECT_AREA_CARDSLOCAL, area_cardslocal_x, area_cardslocal_y, area_cardslocal_w, area_cardslocal_h)
            .rectangle(RECT_SPECIALCOUNTERTEXT, rect_specialscore_text_x, rect_specialscore_text_y, rect_specialscore_text_w, rect_specialscore_text_h)
            .rectangle(RECT_SPECIALCOUNTERBACKGROUND, rect_specialscore_bg_x, rect_specialscore_bg_y, rect_specialscore_bg_w, rect_specialscore_bg_h)
            
            .rectangle(RECT_STACK_DEPOT_0, rect_stack_depot_x, rect_stack_depot_y, Base.SWIDTH, Base.SHEIGHT)
            .rectangle(RECT_STACK_SPECIAL_0, rect_stack_special_x, rect_stack_special_y, Base.SWIDTH, Base.SHEIGHT)
            .rectangle(RECT_STACK_TURNOVER_0, rect_stack_turnover_x, rect_stack_turnover_y, (int) (Base.SWIDTH + 0.5 * Base.CWIDTH), Base.SHEIGHT)
            .rectangle(RECT_STACK_MIDDLE_0, rect_stack_middle_x + 0 * (Base.SWIDTH + Base.SOFFSETX), rect_stack_middle_y, Base.SWIDTH, Base.SHEIGHTMIDDLE)
            .rectangle(RECT_STACK_MIDDLE_1, rect_stack_middle_x + 1 * (Base.SWIDTH + Base.SOFFSETX), rect_stack_middle_y, Base.SWIDTH, Base.SHEIGHTMIDDLE)
            .rectangle(RECT_STACK_MIDDLE_2, rect_stack_middle_x + 2 * (Base.SWIDTH + Base.SOFFSETX), rect_stack_middle_y, Base.SWIDTH, Base.SHEIGHTMIDDLE)
            .rectangle(RECT_STACK_MIDDLE_3, rect_stack_middle_x + 3 * (Base.SWIDTH + Base.SOFFSETX), rect_stack_middle_y, Base.SWIDTH, Base.SHEIGHTMIDDLE)
            .rectangle(RECT_STACK_LAYDOWN_0, area_cardslaydown_x + 0 * (Base.SWIDTH + Base.SOFFSETX), rect_stack_laydown_y, Base.SWIDTH, Base.SHEIGHT)
            .rectangle(RECT_STACK_LAYDOWN_1, area_cardslaydown_x + 1 * (Base.SWIDTH + Base.SOFFSETX), rect_stack_laydown_y, Base.SWIDTH, Base.SHEIGHT)
            .rectangle(RECT_STACK_LAYDOWN_2, area_cardslaydown_x + 2 * (Base.SWIDTH + Base.SOFFSETX), rect_stack_laydown_y, Base.SWIDTH, Base.SHEIGHT)
            .rectangle(RECT_STACK_LAYDOWN_3, area_cardslaydown_x + 3 * (Base.SWIDTH + Base.SOFFSETX), rect_stack_laydown_y, Base.SWIDTH, Base.SHEIGHT)
                       
            .bound(total_w, total_h);
        
        positions_base = builder.build();
    }
    
    static Positions create (int maxWidth, int maxHeight)
    {
        Positions positions = positions_base.calculate(maxWidth, maxHeight);
        return positions;
    }
}
