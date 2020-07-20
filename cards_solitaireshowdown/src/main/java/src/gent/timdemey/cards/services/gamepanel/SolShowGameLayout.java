package gent.timdemey.cards.services.gamepanel;

final class SolShowGameLayout
{    
    public final int max_width;
    public final int max_height;
    public final int cont_width;
    public final int cont_height;
    public final int cont_marginleft;
    public final int cont_marginright;
    public final int cont_margintop;
    public final int cont_marginbottom;

    public final int c_width;
    public final int c_height;
    public final int s_width;
    public final int s_height;
    public final int sc_offsetx;
    public final int sc_offsety;
    public final int c_offsetvisx;
    public final int c_offsetvisy;
    public final int s_offsetx;
    public final int s_offsety;

    public int scoretext_height;

    private SolShowGameLayout(
            int max_width,
            int max_height,
            int cont_width,
            int cont_height,
            int cont_marginleft,
            int cont_marginright,
            int cont_margintop,
            int cont_marginbottom,
            int c_width,
            int c_height,
            int s_width,
            int s_height,
            int sc_offsetx,
            int sc_offsety,
            int c_offsetvisx,
            int c_offsetvisy,
            int s_offsetx,
            int s_offsety,
            int scoretext_height    ,
            act_area_laydown_width,
            act_area_laydown_height,
            act_area_personal_width,
            act_area_personal_height,
            act_area_player_width,
            act_area_player_height 
    )
    {
        this.max_width = max_width;
        this.max_height = max_height;
        this.cont_width = cont_width;
        this.cont_height = cont_height;
        this.cont_marginleft = cont_marginleft;
        this.cont_marginright = cont_marginright;
        this.cont_margintop = cont_margintop;
        this.cont_marginbottom = cont_marginbottom;

        this.c_width = c_width;
        this.c_height = c_height;
        this.s_width = s_width;
        this.s_height = s_height;
        this.sc_offsetx = sc_offsetx;
        this.sc_offsety = sc_offsety;
        this.c_offsetvisx = c_offsetvisx;
        this.c_offsetvisy = c_offsetvisy;
        this.s_offsetx = s_offsetx;
        this.s_offsety = s_offsety;
        this.scoretext_height = scoretext_height;
    }
    
    static SolShowGameLayout create (int maxWidth, int maxHeight)
    {
        // choose multiplier so everything fits according to base ratios, as
        // large as possible

        int act_max_width = maxWidth;
        int act_max_height = maxHeight;
        int ratio_hor = (int) (1.0 * act_max_width / SolShowGameLayoutBaseDef.TWIDTH);
        int ratio_ver = (int) (1.0 * act_max_height / SolShowGameLayoutBaseDef.THEIGHT);

        int ratio = Math.min(ratio_hor, ratio_ver);

        int act_c_width = ratio * SolShowGameLayoutBaseDef.CWIDTH;
        int act_c_height = ratio * SolShowGameLayoutBaseDef.CHEIGHT;
        int act_s_width = ratio * SolShowGameLayoutBaseDef.SWIDTH;
        int act_s_height = ratio * SolShowGameLayoutBaseDef.SHEIGHT;
        int act_sc_offsetx = ratio * SolShowGameLayoutBaseDef.SCOFFSETX;
        int act_sc_offsety = ratio * SolShowGameLayoutBaseDef.SCOFFSETY;
        int act_c_offsetvisx = ratio * SolShowGameLayoutBaseDef.COFFSETX;
        int act_c_offsetvisy = ratio * SolShowGameLayoutBaseDef.COFFSETY;
        int act_s_offsetx = ratio * SolShowGameLayoutBaseDef.SOFFSETX;
        int act_s_offsety = ratio * SolShowGameLayoutBaseDef.SOFFSETY;
        int act_scoretext_height = ratio * SolShowGameLayoutBaseDef.SPSCOREHEIGHT;
        
        int act_area_laydown_width = ratio * SolShowGameLayoutBaseDef.AREA_LAYDOWN_WIDTH;
        int act_area_laydown_height = ratio * SolShowGameLayoutBaseDef.AREA_LAYDOWN_HEIGHT;
        int act_area_personal_width = ratio * SolShowGameLayoutBaseDef.AREA_PERSONAL_HEIGHT;
        int act_area_personal_height = ratio * SolShowGameLayoutBaseDef.AREA_PERSONAL_HEIGHT;
        int act_area_player_width = ratio * SolShowGameLayoutBaseDef.AREA_PLAYER_WIDTH;
        int act_area_player_height = ratio * SolShowGameLayoutBaseDef.AREA_PLAYER_HEIGHT;
        
        
        // remaining space is distributed to the paddings, keeps everything
        // centered
        int act_cont_width = ratio * SolShowGameLayoutBaseDef.TWIDTH;
        int act_cont_marginleft = (act_max_width - act_cont_width) / 2;
        int act_cont_marginright = act_max_width - act_cont_width - act_cont_marginleft;
        int act_cont_height = ratio * SolShowGameLayoutBaseDef.THEIGHT;
        int act_cont_margintop = (act_max_height - act_cont_height) / 2;
        int act_cont_marginbottom = act_max_height - act_cont_height - act_cont_margintop;        
        
        SolShowGameLayout gl = new SolShowGameLayout
        (
                act_max_width,
                act_max_height,
                act_cont_width,
                act_cont_height,
                act_cont_marginleft,
                act_cont_marginright,
                act_cont_margintop,
                act_cont_marginbottom,
                act_c_width,
                act_c_height,
                act_s_width,
                act_s_height,
                act_sc_offsetx,
                act_sc_offsety,
                act_c_offsetvisx,
                act_c_offsetvisy,
                act_s_offsetx,
                act_s_offsety,
                act_scoretext_height,
                act_area_laydown_width,
                act_area_laydown_height,
                act_area_personal_width,
                act_area_personal_height,
                act_area_player_width,
                act_area_player_height 
        );
        return gl;
    }
}
