package gent.timdemey.cards.services.gamepanel;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.cardgame.SolShowCardStackType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IScalingService;
import gent.timdemey.cards.services.scaleman.IScalableComponent;

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

    @Override
    protected void addScalableComponents()
    {
        IScalingService scaleCompServ = Services.get(IScalingService.class);
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();

        super.addScalableComponents();
        
        // cardstack components
        List<ReadOnlyCardStack> cardstacks = cardGame.getCardStacks();
        for (int i = 0; i < cardstacks.size(); i++)
        {
            ReadOnlyCardStack cardstack = cardstacks.get(i);
            IScalableComponent<?> scaleComp = scaleCompServ.getOrCreateScalableComponent(cardstack);
            add(scaleComp);
        }
    }
}
