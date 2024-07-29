package gent.timdemey.cards.services.panels.game;

import gent.timdemey.cards.di.Container;
import java.util.List;
import java.util.UUID;


import gent.timdemey.cards.readonlymodel.ReadOnlyCardGame;
import gent.timdemey.cards.readonlymodel.ReadOnlyCardStack;
import gent.timdemey.cards.services.contract.descriptors.SolitaireComponentTypes;
import gent.timdemey.cards.services.interfaces.IContextService;
import gent.timdemey.cards.services.interfaces.IResourceNameService;
import gent.timdemey.cards.ui.panels.game.CardGamePanelManager;

public class SolitaireGamePanelManager extends CardGamePanelManager
{
    private static final String FILEPATH_CARDSTACK = "stack_solitaire_%s.png";

    public SolitaireGamePanelManager(Container container, IResourceNameService resourceNameService, IContextService contextService)
    {
        super(container, resourceNameService, contextService);
    }
    
    @Override
    public void preload()
    {
        super.preload();
        
        preloadCardStacks();
    }
    
    private void preloadCardStacks()
    {
        String[] stacks = new String[] { SolitaireComponentTypes.DEPOT, SolitaireComponentTypes.LAYDOWN, SolitaireComponentTypes.MIDDLE,
                SolitaireComponentTypes.TURNOVER };

        for (String stack : stacks)
        {
            UUID id = _IdService.createCardStackScalableResourceId(stack);
            String filename = String.format(FILEPATH_CARDSTACK, stack.toLowerCase());
            preloadImage(id, filename);
        }
    }
    
    @Override
    public void addComponentCreators(List<Runnable> compCreators)
    {
        super.addComponentCreators(compCreators);

        ReadOnlyCardGame cardGame = _ContextService.getThreadContext().getReadOnlyState().getCardGame();
        // cardstack comp2jcomp
        List<ReadOnlyCardStack> cardstacks = cardGame.getCardStacks();
        for (int i = 0; i < cardstacks.size(); i++)
        {
            ReadOnlyCardStack cardstack = cardstacks.get(i);
            createJSImage(cardstack);
        }
    }
}
