package gent.timdemey.cards.services.gamepanel;

final class SolShowGameLayoutBaseDef
{
    static final int CWIDTH = 16;                           // card width
    static final int CHEIGHT = 22;                          // card height
    static final int SWIDTH = 18;                           // stack width
    static final int SHEIGHT = 24;                          // base stack height
    static final int SMIDDLEHEIGHT = 60;                    // middle stack height
    
    static final int SCOFFSETX = 1;                         // offset for a card relative to its stack, horizontally
    static final int SCOFFSETY = 1;                         // offset for a card relative to its stack, vertically
    static final int COFFSETX = 4;                          // offset for a card on top of another card, horizontally
    static final int COFFSETY = 3;                          // offset for a card on top of another card, vertically
    static final int SOFFSETX = 0;                          // offset for a cardstack relative to another cardstack, horizontally 
    static final int SOFFSETY = 1;                          // offset for a cardstack relative to another cardstack, vertically
    static final int SPSCOREHEIGHT = 8;                     // special score text height
    
    static final int AREAMARGIN_X = 2;                          // inter-region margin, horizontally
    static final int AREAMARGIN_Y = 3;                          // inter-region margin, vertically    
    static final int AREAPADDING_X = 1;
    static final int AREAPADDING_Y = 1;
    static final int AREA_LAYDOWN_WIDTH = 8 * SWIDTH + 7 * SOFFSETX;
    static final int AREA_LAYDOWN_HEIGHT = SHEIGHT;    
    static final int AREA_PERSONAL_WIDTH = AREA_LAYDOWN_WIDTH;
    static final int AREA_PERSONAL_HEIGHT = SMIDDLEHEIGHT + 2 * AREAPADDING_Y;

    static final int AREA_VS_WIDTH = 40; 
    static final int AREA_VS_HEIGHT = 10;    
    static final int AREA_PLAYER_WIDTH = AREA_VS_WIDTH;                           // player visualization area width
    static final int TWIDTH = AREA_PLAYER_WIDTH + AREA_LAYDOWN_WIDTH + AREAMARGIN_X;    // total width
    static final int THEIGHT = 2 * AREA_PERSONAL_HEIGHT + 1 * AREA_LAYDOWN_HEIGHT + 2 * AREAMARGIN_Y;  // total height
    static final int AREA_PLAYER_HEIGHT = (THEIGHT - AREA_VS_HEIGHT - 2 * AREAMARGIN_Y) / 2;                          // player visualization area height

  
    private SolShowGameLayoutBaseDef(){}
}
