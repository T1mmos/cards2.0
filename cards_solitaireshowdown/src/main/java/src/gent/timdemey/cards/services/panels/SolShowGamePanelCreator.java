package gent.timdemey.cards.services.panels;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.interfaces.ISolShowIdService;
import gent.timdemey.cards.services.resources.SolShowResourceDefines;

public class SolShowGamePanelCreator extends GamePanelManager
{
    private void preloadImages()
    {
        ISolShowIdService idServ = Services.get(ISolShowIdService.class);

        // cardstacks depot, laydown, middle
        String[] solstacks = new String[]
        { SolShowCardStackType.DEPOT, SolShowCardStackType.LAYDOWN, SolShowCardStackType.MIDDLE };        
        for (String stack : solstacks)
        {
            UUID id = idServ.createCardStackScalableResourceId(stack);
            String filename = String.format(SolShowResourceDefines.FILEPATH_IMG_CARDSTACK, stack.toLowerCase());
            preloadImage(id, filename);
        }
        // cardstacks turnover and special
        UUID rid_turnover = idServ.createCardStackScalableResourceId(SolShowCardStackType.TURNOVER);
        preloadImage(rid_turnover, SolShowResourceDefines.FILEPATH_IMG_CARDSTACK_TURNOVER);        
        UUID rid_special = idServ.createCardStackScalableResourceId(SolShowCardStackType.SPECIAL);
        preloadImage(rid_special, SolShowResourceDefines.FILEPATH_IMG_CARDSTACK_SPECIAL);
        
        // special stack background star images
        UUID rid_star_remote = idServ.createSpecialBackgroundResourceId(true);
        UUID rid_star_local = idServ.createSpecialBackgroundResourceId(false);
        preloadImage(rid_star_remote, SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_SPECIAL_REMOTE);
        preloadImage(rid_star_local, SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_SPECIAL_LOCAL);
        
        // player-specific backgrounds and VS background
        UUID rid_pbgremote = idServ.createPlayerBgResourceId(true);
        UUID rid_pbglocal = idServ.createPlayerBgResourceId(false);    
        UUID rid_cabgremote = idServ.createCardAreaBgResourceId(true);
        UUID rid_cabglocal = idServ.createCardAreaBgResourceId(false);
        UUID rid_vs = idServ.createVsResourceId();
        preloadImage(rid_pbgremote, SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_PLAYER_REMOTE);
        preloadImage(rid_pbglocal,  SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_PLAYER_LOCAL);
        preloadImage(rid_cabgremote, SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_CARDAREA_REMOTE);
        preloadImage(rid_cabglocal, SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_CARDAREA_LOCAL);
        preloadImage(rid_vs, SolShowResourceDefines.FILEPATH_IMG_BACKGROUND_VS);
    }
    
}
