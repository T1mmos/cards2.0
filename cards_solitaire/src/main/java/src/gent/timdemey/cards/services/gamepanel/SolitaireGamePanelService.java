package gent.timdemey.cards.services.gamepanel;

import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.services.cardgame.SolitaireCardStackType;
import gent.timdemey.cards.services.interfaces.IIdService;

public class SolitaireGamePanelService extends GamePanelService
{
    private static final String FILEPATH_CARDSTACK = "stack_%s.png";

    @Override
    public void preload()
    {
        super.preload();

        preloadCardStacks();
    }

    protected void preloadCardStacks()
    {
        IIdService idServ = Services.get(IIdService.class);

        String[] stacks = new String[] { SolitaireCardStackType.DEPOT, SolitaireCardStackType.LAYDOWN, SolitaireCardStackType.MIDDLE,
                SolitaireCardStackType.TURNOVER };

        for (String stack : stacks)
        {
            UUID id = idServ.createCardStackResourceId(stack);
            String filename = String.format(FILEPATH_CARDSTACK, stack.toLowerCase());
            preloadImage(id, filename);
        }
    }
}
