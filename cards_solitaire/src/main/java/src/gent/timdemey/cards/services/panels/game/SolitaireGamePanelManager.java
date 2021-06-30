package gent.timdemey.cards.services.panels.game;

import java.util.List;
import java.util.UUID;

import gent.timdemey.cards.Services;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.contract.descriptors.SolitaireComponentTypes;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IIdService;
import gent.timdemey.cards.ui.panels.game.CardGamePanelManager;

public class SolitaireGamePanelManager extends CardGamePanelManager
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

        String[] stacks = new String[] { SolitaireComponentTypes.DEPOT, SolitaireComponentTypes.LAYDOWN, SolitaireComponentTypes.MIDDLE,
                SolitaireComponentTypes.TURNOVER };

        for (String stack : stacks)
        {
            UUID id = idServ.createCardStackScalableResourceId(stack);
            String filename = String.format(FILEPATH_CARDSTACK, stack.toLowerCase());
            preloadImage(id, filename);
        }
    }
    
    @Override
    public void createComponentsAsync()
    {
        ReadOnlyCardGame cardGame = Services.get(IContextService.class).getThreadContext().getReadOnlyState().getCardGame();

        super.createComponentsAsync();
        
        // cardstack comp2jcomp
        List<ReadOnlyCardStack> cardstacks = cardGame.getCardStacks();
        for (int i = 0; i < cardstacks.size(); i++)
        {
            ReadOnlyCardStack cardstack = cardstacks.get(i);
            createJSImage(cardstack);
        }
    }
}
