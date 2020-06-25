package gent.timdemey.cards.services.gamepanel;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCard;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.cardgame.SolitaireCardStackType;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.services.interfaces.IScalableComponentService;
import gent.timdemey.cards.services.scaleman.IScalableComponent;

public class SolitaireGamePanelService extends GamePanelService
{
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

        String[] stacks = new String[] { SolitaireCardStackType.DEPOT, SolitaireCardStackType.LAYDOWN, SolitaireCardStackType.MIDDLE,
                SolitaireCardStackType.TURNOVER };

        for (String stack : stacks)
        {
            UUID id = idServ.createCardStackResourceId(stack);
            String filename = String.format(FILEPATH_CARDSTACK, stack.toLowerCase());
            preloadImage(id, filename);
        }
    }
    
    @Override
    protected void addScalableComponents()
    {
        IScalableComponentService scaleCompServ = Services.get(IScalableComponentService.class);

        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();

        // card components
        List<ReadOnlyCard> cards = cardGame.getCards();
        for (int i = 0; i < cards.size(); i++)
        {
            ReadOnlyCard card = cards.get(i);
            IScalableComponent scaleComp = scaleCompServ.getOrCreateScalableComponent(card);
            add(scaleComp);
        }
        
        // cardstack components
        List<ReadOnlyCardStack> cardstacks = cardGame.getCardStacks();
        for (int i = 0; i < cardstacks.size(); i++)
        {
            ReadOnlyCardStack cardstack = cardstacks.get(i);
            IScalableComponent scaleComp = scaleCompServ.getOrCreateScalableComponent(cardstack);
            add(scaleComp);
        }
    }

}
