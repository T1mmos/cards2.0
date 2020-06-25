package gent.timdemey.cards.services.gamepanel;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.interfaces.IIdService;

public class SolShowGamePanelService extends GamePanelService
{
    // reuse some of the Solitaire sprites for the time being
    private static final String FILEPATH_CARDSTACK = "stack_solitaire_%s.png";

    @Override
    public void preload()
    {
        super.preload();

        preloadCardStacks();
    }

    private void preloadCardStacks()
    {
        IIdService idServ = Services.get(IIdService.class);

        String[] solstacks = new String[]
        { SolShowCardStackType.DEPOT, SolShowCardStackType.LAYDOWN, SolShowCardStackType.MIDDLE };
        
        for (String stack : solstacks)
        {
            UUID id = idServ.createCardStackResourceId(stack);
            String filename = String.format(FILEPATH_CARDSTACK, stack.toLowerCase());
            preloadImage(id, filename);
        }
        
        UUID resId_turnover = idServ.createCardStackResourceId(SolShowCardStackType.TURNOVER);
        String filename_turnover = "stack_solshow_turnover.png";
        preloadImage(resId_turnover, filename_turnover);
        
        UUID resId_special = idServ.createCardStackResourceId(SolShowCardStackType.SPECIAL);
        String filename_special = "stack_solshow_special.png";
        preloadImage(resId_special, filename_special);
    }

}
